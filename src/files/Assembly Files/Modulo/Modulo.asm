// computes RAM[0] % RAM[1] -> RAM[2]
//@precondition: RAM[0], RAM[1] > 0
//@destroys: RAM[0]
@R2
M=0
(SUBTRACT_AGAIN)
@R0
D=M
@R1
D=D-M
@END
D;JLT
@R0
M=D
@R2
M=M+1
@SUBTRACT_AGAIN
0;JMP
(END)
@END
0;JMP