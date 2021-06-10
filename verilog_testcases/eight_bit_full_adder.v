module EightBit_FullAdder(a, b, c_in, c_out, sum);

input [7:0] a;
input [7:0] b;
input c_in;
output c_out;
output [7:0] sum;

wire intermediate_carry;

FourBit_FullAdder a1(a[3:0], b[3:0], c_in, intermediate_carry, sum[3:0]);
FourBit_FullAdder a2(a[7:4], b[7:4], intermediate_carry, c_out, sum[7:4]);
endmodule