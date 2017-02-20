<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output indent="no" method="text" omit-xml-declaration="yes"/>
<xsl:template match="/"># Changelog
<xsl:for-each select="/document/body/release">
## <xsl:value-of select="@version"/> (<xsl:value-of select="@date"/>)
<xsl:for-each select="action">* <xsl:value-of select="text()"/><xsl:text>&#xa;</xsl:text></xsl:for-each>
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>