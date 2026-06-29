import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:multi_step_form_mobile/features/hello/data/hello_view_repository.dart';
import 'package:multi_step_form_mobile/features/hello/presentation/cubit/hello_cubit.dart';
import 'package:multi_step_form_mobile/features/hello/presentation/cubit/hello_state.dart';

class HelloPage extends StatelessWidget {
  const HelloPage({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (_) => HelloCubit(repository: HelloViewRepository())..load(),
      child: const HelloView(),
    );
  }
}

class HelloView extends StatelessWidget {
  const HelloView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Servewright Hello')),
      body: Center(
        child: BlocBuilder<HelloCubit, HelloState>(
          builder: (context, state) {
            return switch (state) {
              HelloInitial() || HelloLoading() => const CircularProgressIndicator(),
              HelloLoaded(:final content) => content,
              HelloError(:final message) => Text('Failed to load view: $message'),
            };
          },
        ),
      ),
    );
  }
}
