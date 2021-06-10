module always_combi(input a, b, c, output reg x);
  always @(a, b, c)
  begin
    x = (a & b) | c;
  end
endmodule