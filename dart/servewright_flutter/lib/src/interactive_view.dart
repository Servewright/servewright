import 'dart:async';

import 'package:flutter/widgets.dart';
import 'package:servewright_flutter/src/action_client.dart';
import 'package:servewright_flutter/src/binding.dart';
import 'package:servewright_flutter/src/renderer.dart';
import 'package:servewright_flutter/src/transition.dart';
import 'package:servewright_flutter/src/transport.dart';
import 'package:servewright_flutter/src/types.dart';

class ServewrightBinding extends InheritedWidget {
  const ServewrightBinding({
    super.key,
    required this.controller,
    required super.child,
  });

  final BindingController controller;

  static BindingController? maybeOf(BuildContext context) {
    return context.dependOnInheritedWidgetOfExactType<ServewrightBinding>()?.controller;
  }

  @override
  bool updateShouldNotify(ServewrightBinding oldWidget) =>
      oldWidget.controller != controller;
}

class BindingController extends ChangeNotifier {
  BindingController({
    required ServewrightView initialView,
    required Registry registry,
    ActionClient? actionClient,
    ServewrightTransport? transport,
  })  : _view = initialView,
        _registry = registry,
        _actionClient = actionClient ?? ActionClient(),
        _transport = transport ?? ImmediateTransport(const []),
        _values = BindingTree.extractInitialValues(initialView.root) {
    _transportSubscription = _transport.connect(
      _view.screen,
      onTransition: applyTransition,
    );
  }

  ServewrightView _view;
  final Registry _registry;
  final ActionClient _actionClient;
  final ServewrightTransport _transport;
  StreamSubscription<String>? _transportSubscription;
  Map<String, String> _values;
  final Set<String> _forceOnChange = {};
  final Set<String> _validating = {};

  ServewrightView get view => _view;
  Map<String, String> get values => Map.unmodifiable(_values);
  Set<String> get validating => Set.unmodifiable(_validating);

  String valueFor(String fieldId) => _values[fieldId] ?? '';

  List<String> errorsFor(String fieldId) => BindingTree.readFieldErrors(_view.root, fieldId);

  bool isValidating(String fieldId) => _validating.contains(fieldId);

  String get primaryActionTarget =>
      BindingTree.primaryActionTarget(_view.root) ?? 'signup';

  bool shouldUseOnChange(String fieldId, ServewrightNode field) =>
      _forceOnChange.contains(fieldId) ||
      BindingTree.effectiveTrigger(field, _forceOnChange.contains(fieldId)) == 'onChange';

  void setFieldValue(String fieldId, String value) {
    _values = {..._values, fieldId: value};
    notifyListeners();
  }

  Future<void> onFieldBlur(String fieldId, ServewrightNode field, String actionTarget) async {
    final errors = BindingValidation.validateInputNode(field, valueFor(fieldId));
    if (errors.isNotEmpty) {
      _forceOnChange.add(fieldId);
    }

    if (field.props['asyncValidation'] == true) {
      final form = BindingTree.findFormByActionTarget(_view.root, actionTarget);
      if (form == null) {
        return;
      }
      _validating.add(fieldId);
      notifyListeners();
      try {
        await _dispatch(
          ServewrightAction(
            type: 'asyncValidate',
            target: actionTarget,
            screen: _view.screen,
            stateVersion: _view.stateVersion,
            payload: {fieldId: valueFor(fieldId)},
          ),
        );
      } finally {
        _validating.remove(fieldId);
        notifyListeners();
      }
    }
  }

  void onFieldChange(String fieldId, ServewrightNode field, String value) {
    setFieldValue(fieldId, value);
    if (!shouldUseOnChange(fieldId, field)) {
      return;
    }
    final errors = BindingValidation.validateInputNode(field, value);
    if (errors.isNotEmpty) {
      _forceOnChange.add(fieldId);
    }
  }

  Future<void> submitForm(String actionTarget) async {
    final form = BindingTree.findFormByActionTarget(_view.root, actionTarget);
    if (form == null) {
      return;
    }

    await _dispatch(
      ServewrightAction(
        type: 'submit',
        target: actionTarget,
        screen: _view.screen,
        stateVersion: _view.stateVersion,
        payload: BindingTree.collectFormPayload(form, _values),
      ),
    );
  }

  Future<void> _dispatch(ServewrightAction action) async {
    final response = await _actionClient.postAction(action);
    if (response.transition != null) {
      applyTransition(response.transition!);
      return;
    }
    if (response.view != null) {
      _view = response.view!;
      _values = BindingTree.extractInitialValues(_view.root);
      _forceOnChange.clear();
      notifyListeners();
    }
  }

  void applyTransition(ServewrightTransition transition) {
    try {
      final dirty = TransitionApplier.collectDirtyFields(_view, _values);
      _view = TransitionApplier.apply(_view, transition, dirtyFields: dirty);
      notifyListeners();
    } on TransitionDesyncError {
      _resync();
    }
  }

  Future<void> _resync() async {
    _view = await _actionClient.fetchView(_view.screen);
    notifyListeners();
  }

  @override
  void dispose() {
    _transportSubscription?.cancel();
    super.dispose();
  }

  Widget render() {
    final renderer = createRenderer(_registry);
    final displayView = ServewrightView(
      servewrightVersion: _view.servewrightVersion,
      schemaVersion: _view.schemaVersion,
      screen: _view.screen,
      stateVersion: _view.stateVersion,
      root: BindingTree.mergeLocalValues(_view.root, _values),
    );
    return ServewrightBinding(
      controller: this,
      child: renderer.render(displayView),
    );
  }
}

class ServewrightInteractiveView extends StatefulWidget {
  const ServewrightInteractiveView({
    super.key,
    required this.controller,
  });

  final BindingController controller;

  @override
  State<ServewrightInteractiveView> createState() => _ServewrightInteractiveViewState();
}

class _ServewrightInteractiveViewState extends State<ServewrightInteractiveView> {
  @override
  void initState() {
    super.initState();
    widget.controller.addListener(_onChanged);
  }

  @override
  void dispose() {
    widget.controller.removeListener(_onChanged);
    super.dispose();
  }

  void _onChanged() {
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return widget.controller.render();
  }
}
