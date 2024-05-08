package net.sf.mpxj;

import java.nio.charset.Charset;

public interface HasCharset
{
   public void setCharset(Charset charset);

   public Charset getCharset();
}
