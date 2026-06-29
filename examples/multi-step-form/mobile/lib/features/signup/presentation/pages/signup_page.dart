import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:servewright_flutter/servewright_flutter.dart';

import '../cubit/signup_cubit.dart';

class SignupPage extends StatelessWidget {
  const SignupPage({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<SignupCubit, SignupState>(
      builder: (context, state) {
        return switch (state) {
          SignupLoading() => const Center(child: CircularProgressIndicator()),
          SignupFailure(:final message) => Center(child: Text(message)),
          SignupReady(:final controller) => ServewrightInteractiveView(controller: controller),
        };
      },
    );
  }
}
