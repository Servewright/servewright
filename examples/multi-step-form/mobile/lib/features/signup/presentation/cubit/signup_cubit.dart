import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:servewright_flutter/servewright_flutter.dart';
import 'package:servewright_flutter_material/servewright_flutter_material.dart';

import '../data/signup_view_repository.dart';

sealed class SignupState {}

class SignupLoading extends SignupState {}

class SignupReady extends SignupState {
  SignupReady(this.controller);

  final BindingController controller;
}

class SignupFailure extends SignupState {
  SignupFailure(this.message);

  final String message;
}

class SignupCubit extends Cubit<SignupState> {
  SignupCubit(this._repository) : super(SignupLoading());

  final SignupViewRepository _repository;

  Future<void> load() async {
    emit(SignupLoading());
    try {
      final view = await _repository.fetchSignupView();
      final registry = createRegistry();
      registerMaterialPrimitives(registry);
      emit(
        SignupReady(
          BindingController(
            initialView: view,
            registry: registry,
            actionClient: ActionClient(
              actionUrl: 'http://localhost:8080/servewright/action',
              viewUrl: 'http://localhost:8080/servewright/view',
            ),
            transport: SseTransport(
              streamUrl: 'http://localhost:8080/servewright/stream',
            ),
          ),
        ),
      );
    } catch (error) {
      emit(SignupFailure('$error'));
    }
  }
}
