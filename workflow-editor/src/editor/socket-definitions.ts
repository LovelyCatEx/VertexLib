import {ClassicPreset} from "rete";

export interface IGraphSocket {
  isCompatibleWith(socket: ClassicPreset.Socket): boolean;
}

export class TriggerSocket extends ClassicPreset.Socket implements IGraphSocket{
  constructor() {
    super("Trigger");
  }

  isCompatibleWith(socket: ClassicPreset.Socket) {
    return socket instanceof TriggerSocket;
  }
}

export class ParameterSocket extends ClassicPreset.Socket implements IGraphSocket {
  constructor() {
    super("Parameter");
  }

  isCompatibleWith(socket: ClassicPreset.Socket) {
    return socket instanceof ParameterSocket;
  }
}

export const triggerSocket = new TriggerSocket();
export const parameterSocket = new ParameterSocket();