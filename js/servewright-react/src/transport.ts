import type { Transition } from "./types.js";

export interface TransportHandlers {
  onTransition: (transition: Transition) => void;
  onError?: (error: unknown) => void;
}

export interface Transport {
  connect(screen: string, handlers: TransportHandlers): () => void;
}

export class SseTransport implements Transport {
  constructor(private readonly streamUrl = "/servewright/stream") {}

  connect(screen: string, handlers: TransportHandlers): () => void {
    const source = new EventSource(`${this.streamUrl}/${encodeURIComponent(screen)}`);

    source.addEventListener("transition", (event) => {
      try {
        const transition = JSON.parse((event as MessageEvent<string>).data) as Transition;
        handlers.onTransition(transition);
      } catch (error) {
        handlers.onError?.(error);
      }
    });

    source.onerror = (error) => {
      handlers.onError?.(error);
    };

    return () => source.close();
  }
}

export class ImmediateTransport implements Transport {
  constructor(private readonly transitions: Transition[] = []) {}

  connect(_screen: string, handlers: TransportHandlers): () => void {
    for (const transition of this.transitions) {
      handlers.onTransition(transition);
    }
    return () => undefined;
  }
}
