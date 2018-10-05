# JBGPL-Compiler
A compiler for a potentially long-term project of mine, a "Java-Based General Purpose Language."

The eventual goal is to compile .jbgpl files into MASM assembly files using, ideally, the .386 instruction set.
  Machine code assembled from .386 instruction set files should be able to run on any Intel processor.
  
However, as I continue to learn about the compilation process, work out details, and clean up my code,
the current version aims to compile from "Jack" to "Hack," a high-level and low-level language respectively
which are specified by https://www.nand2tetris.org/. A "Hack" CPU emulator is included in the "tools" folder; simply
run the CPUEmulator.bat file. You can load either Hack Assembly .asm or Hack Binary .hack files into the emulator, it runs both.

I apologize in advance for the regex that matches comments in the Tokenizer.

DISCLAIMER: The entirety of the tools folder was copied directly from the nand2tetris Software Suite, which can be found at
https://www.nand2tetris.org/software. Other files which were included from the Software Suite contain a comment
at the top which says so. These comments are unmodified. Files which do not contain these comments were either written,
translated, or assembled by me.
