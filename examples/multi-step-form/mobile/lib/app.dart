import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:multi_step_form_mobile/features/signup/data/signup_view_repository.dart';
import 'package:multi_step_form_mobile/features/signup/presentation/cubit/signup_cubit.dart';
import 'package:multi_step_form_mobile/features/signup/presentation/pages/signup_page.dart';

class App extends StatelessWidget {
  const App({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: BlocProvider(
        create: (_) => SignupCubit(SignupViewRepository())..load(),
        child: const SignupPage(),
      ),
    );
  }
}
