<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output indent="no" method="text" omit-xml-declaration="yes"/>
<xsl:template match="/"># Changelog

## NOTE
From version 14.0.0 onwards the `net.sf.mpxj`, `net.sf.mpxj-for-csharp` and `net.sf.mpxj-for-vb` packages are
no longer distributed. Please use the `MPXJ.Net` package instead.

<xsl:for-each select="/document/body/release">
## <xsl:value-of select="@version"/> (<xsl:value-of select="@date"/>)
<xsl:for-each select="action">
<xsl:choose>
<xsl:when test="not(@issue)">* <xsl:value-of select="text()"/><xsl:text>&#xa;</xsl:text></xsl:when>
<xsl:otherwise>* Fixed [Issue <xsl:value-of select="@issue"/>](https://sourceforge.net/p/mpxj/bugs/<xsl:value-of select="@issue"/>): <xsl:value-of select="text()"/><xsl:text>&#xa;</xsl:text></xsl:otherwise>
</xsl:choose>
</xsl:for-each>
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>