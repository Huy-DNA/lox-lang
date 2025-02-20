#include "chunk.h"
#include "common.h"
#include "debug.h"

int main(int argc, const char *argv[]) {
  Chunk chunk;
  initChunk(&chunk);
  for (int i = 0; i < 300; ++i) {
    writeConstant(&chunk, i, 0);
  }
  writeChunk(&chunk, OP_RETURN, 0);
  disassembleChunk(&chunk, "test chunk");
  freeChunk(&chunk);
  return 0;
}
