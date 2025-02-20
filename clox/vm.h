#ifndef clox_vm_h
#define clox_vm_h

#include "chunk.h"

typedef struct {
  Chunk *chunk;
} VirtualMachine;

void initVM(VirtualMachine *vm);
void freeVM(VirtualMachine *vm);

#endif
