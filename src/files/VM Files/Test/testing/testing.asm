@255
D=A
@SP
M=D
@Sys.init
0;JMP
(RETURNER)
@5
D=A
@LCL
A=M-D
D=M
@R13
M=D
@SP
A=M
D=M
@ARG
A=M
M=D
D=A
@SP
M=D
@LCL
D=M
@R14
AM=D-1
D=M
@THAT
M=D
@R14
AM=M-1
D=M
@THIS
M=D
@R14
AM=M-1
D=M
@ARG
M=D
@R14
AM=M-1
D=M
@LCL
M=D
@R13
A=M
0;JMP
(STACKER)
@SP
AM=M+1
M=D
@LCL
D=M
@SP
AM=M+1
M=D
@ARG
D=M
@SP
AM=M+1
M=D
@THIS
D=M
@SP
AM=M+1
M=D
@THAT
D=M
@SP
AM=M+1
M=D
@4
D=A
@R13
D=D+M
@SP
D=M-D
@ARG
M=D
@SP
D=M+1
@LCL
M=D
@R14
A=M
0;JMP
(Factorial.factorial)
@0
D=A
@ARG
A=M+D
D=M
@SP
AM=M+1
M=D
@1
D=A
@SP
AM=M+1
M=D
@2
D=A
@R13
M=D
@Factorial.recur
D=A
@R14
M=D
@RETURN_ADDRESS_0
D=A
@STACKER
0;JMP
(RETURN_ADDRESS_0)
@RETURNER
0;JMP
(Factorial.recur)
@0
D=A
@ARG
A=M+D
D=M
@SP
AM=M+1
M=D
@1
D=A
@SP
AM=M+1
M=D
@SP
A=M
D=-M
@SP
AM=M-1
D=D+M
@Factorial.recur_if_true0
D;JEQ
@SP
A=M
M=0
@continue_Factorial.recur0
0;JMP
(Factorial.recur_if_true0)
@SP
A=M
M=-1
(continue_Factorial.recur0)
@SP
M=M-1
A=M+1
D=M
@Factorial.recur$IF_TRUE
D;JNE
@0
D=A
@ARG
A=M+D
D=M
@SP
AM=M+1
M=D
@1
D=A
@SP
AM=M+1
M=D
@SP
A=M
D=-M
@SP
AM=M-1
M=D+M
@0
D=A
@ARG
A=M+D
D=M
@SP
AM=M+1
M=D
@1
D=A
@ARG
A=M+D
D=M
@SP
AM=M+1
M=D
@2
D=A
@R13
M=D
@Sys.multiply
D=A
@R14
M=D
@RETURN_ADDRESS_1
D=A
@STACKER
0;JMP
(RETURN_ADDRESS_1)
@2
D=A
@R13
M=D
@Factorial.recur
D=A
@R14
M=D
@RETURN_ADDRESS_2
D=A
@STACKER
0;JMP
(RETURN_ADDRESS_2)
@RETURNER
0;JMP
(Factorial.recur$IF_TRUE)
@1
D=A
@ARG
A=M+D
D=M
@SP
AM=M+1
M=D
@RETURNER
0;JMP
(Main.main)
@3
D=A
@SP
AM=M+1
M=D
@1
D=A
@R13
M=D
@Factorial.factorial
D=A
@R14
M=D
@RETURN_ADDRESS_3
D=A
@STACKER
0;JMP
(RETURN_ADDRESS_3)
@5
D=A
@0
D=D+A
@R13
M=D
@SP
M=M-1
A=M+1
D=M
@R13
A=M
M=D
@0
D=A
@SP
AM=M+1
M=D
@RETURNER
0;JMP
(Sys.init)
@0
D=A
@R13
M=D
@Main.main
D=A
@R14
M=D
@RETURN_ADDRESS_4
D=A
@STACKER
0;JMP
(RETURN_ADDRESS_4)
(Sys.init$WHILE)
@Sys.init$WHILE
0;JMP
(Sys.multiply)
@0
D=A
@SP
AM=M+1
M=D
(Sys.multiply$LOOP)
@0
D=A
@ARG
A=M+D
D=M
@SP
AM=M+1
M=D
@0
D=A
@SP
AM=M+1
M=D
@SP
A=M
D=-M
@SP
AM=M-1
D=D+M
@Sys.multiply_if_true0
D;JEQ
@SP
A=M
M=0
@continue_Sys.multiply0
0;JMP
(Sys.multiply_if_true0)
@SP
A=M
M=-1
(continue_Sys.multiply0)
@SP
M=M-1
A=M+1
D=M
@Sys.multiply$BREAK
D;JNE
@0
D=A
@ARG
A=M+D
D=M
@SP
AM=M+1
M=D
@1
D=A
@SP
AM=M+1
M=D
@SP
A=M
D=-M
@SP
AM=M-1
M=D+M
@ARG
D=M
@0
D=D+A
@R13
M=D
@SP
M=M-1
A=M+1
D=M
@R13
A=M
M=D
@1
D=A
@ARG
A=M+D
D=M
@SP
AM=M+1
M=D
@0
D=A
@LCL
A=M+D
D=M
@SP
AM=M+1
M=D
@SP
A=M
D=M
@SP
AM=M-1
M=D+M
@LCL
D=M
@0
D=D+A
@R13
M=D
@SP
M=M-1
A=M+1
D=M
@R13
A=M
M=D
@Sys.multiply$LOOP
0;JMP
(Sys.multiply$BREAK)
@0
D=A
@LCL
A=M+D
D=M
@SP
AM=M+1
M=D
@RETURNER
0;JMP
