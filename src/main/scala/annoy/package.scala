import com.github.fommil.netlib.BLAS

/**
  * Created by emeth on 2016. 9. 6..
  */
package object annoy {
  type T = Float
  type S = Int
  val Zero = 0f
  val One = 1f
  val blas = BLAS.getInstance()

  def showUpdate(text: String, xs: Any*): Unit = Console.err.print(text.format(xs: _*))
}
