module double_assign(input a, b, c, output x, y);
  assign x = (a & b) | c;
  assign y = (a ^ b) & ~c;
endmodule