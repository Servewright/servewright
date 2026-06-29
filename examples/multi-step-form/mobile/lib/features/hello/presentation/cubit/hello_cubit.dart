import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:multi_step_form_mobile/core/servewright/servewright_renderer_factory.dart';
import 'package:multi_step_form_mobile/features/hello/data/hello_view_repository.dart';
import 'package:multi_step_form_mobile/features/hello/presentation/cubit/hello_state.dart';
import 'package:servewright_flutter/servewright_flutter.dart';

class HelloCubit extends Cubit<HelloState> {
  HelloCubit({
    required HelloViewRepository repository,
    Renderer Function()? createRenderer,
  })  : _repository = repository,
        _renderer = (createRenderer ?? ServewrightRendererFactory.create)(),
        super(const HelloInitial());

  final HelloViewRepository _repository;
  final Renderer _renderer;

  Future<void> load() async {
    emit(const HelloLoading());
    try {
      final view = await _repository.fetchHelloView();
      emit(HelloLoaded(_renderer.render(view)));
    } catch (error) {
      emit(HelloError(error.toString()));
    }
  }
}
