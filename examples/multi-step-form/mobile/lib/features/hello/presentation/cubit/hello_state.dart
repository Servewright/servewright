import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

sealed class HelloState extends Equatable {
  const HelloState();

  @override
  List<Object?> get props => [];
}

final class HelloInitial extends HelloState {
  const HelloInitial();
}

final class HelloLoading extends HelloState {
  const HelloLoading();
}

final class HelloLoaded extends HelloState {
  const HelloLoaded(this.content);

  final Widget content;

  @override
  List<Object?> get props => const [];
}

final class HelloError extends HelloState {
  const HelloError(this.message);

  final String message;

  @override
  List<Object?> get props => [message];
}
