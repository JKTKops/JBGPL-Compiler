@17
D=A
@SP
AM=M+1
M=D
@17
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
@if_true0
D;JEQ
@SP
A=M
M=0
@continue0
0;JMP
(if_true0)
@SP
A=M
M=-1
(continue0)
@17
D=A
@SP
AM=M+1
M=D
@16
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
@if_true1
D;JEQ
@SP
A=M
M=0
@continue1
0;JMP
(if_true1)
@SP
A=M
M=-1
(continue1)
@16
D=A
@SP
AM=M+1
M=D
@17
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
@if_true2
D;JEQ
@SP
A=M
M=0
@continue2
0;JMP
(if_true2)
@SP
A=M
M=-1
(continue2)
@892
D=A
@SP
AM=M+1
M=D
@891
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
@if_true3
D;JLT
@SP
A=M
M=0
@continue3
0;JMP
(if_true3)
@SP
A=M
M=-1
(continue3)
@891
D=A
@SP
AM=M+1
M=D
@892
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
@if_true4
D;JLT
@SP
A=M
M=0
@continue4
0;JMP
(if_true4)
@SP
A=M
M=-1
(continue4)
@891
D=A
@SP
AM=M+1
M=D
@891
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
@if_true5
D;JLT
@SP
A=M
M=0
@continue5
0;JMP
(if_true5)
@SP
A=M
M=-1
(continue5)
@32767
D=A
@SP
AM=M+1
M=D
@32766
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
@if_true6
D;JGT
@SP
A=M
M=0
@continue6
0;JMP
(if_true6)
@SP
A=M
M=-1
(continue6)
@32766
D=A
@SP
AM=M+1
M=D
@32767
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
@if_true7
D;JGT
@SP
A=M
M=0
@continue7
0;JMP
(if_true7)
@SP
A=M
M=-1
(continue7)
@32766
D=A
@SP
AM=M+1
M=D
@32766
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
@if_true8
D;JGT
@SP
A=M
M=0
@continue8
0;JMP
(if_true8)
@SP
A=M
M=-1
(continue8)
@57
D=A
@SP
AM=M+1
M=D
@31
D=A
@SP
AM=M+1
M=D
@53
D=A
@SP
AM=M+1
M=D
@SP
A=M
D=M
@SP
AM=M-1
M=D+M
@112
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
@SP
A=M
M=-M
@SP
A=M
D=M
@SP
AM=M-1
M=D&M
@82
D=A
@SP
AM=M+1
M=D
@SP
A=M
D=M
@SP
AM=M-1
M=D|M
@SP
A=M
M=!M

(TERMINATE)
@TERMINATE
0;JMP