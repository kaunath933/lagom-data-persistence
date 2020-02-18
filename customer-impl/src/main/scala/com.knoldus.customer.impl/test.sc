def foo(n: Int, v: Int) =
  for (i <- 0 until n;
       j <- 0 until n if i + j == v)
    yield (i, j)

foo(10,10).foreach{
  case (i,j) => println(s"($i, $j)")
}