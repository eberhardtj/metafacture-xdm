<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output indent="yes"/>

    <xsl:template match="/">
        <xsl:apply-templates select="animals"/>
    </xsl:template>

    <xsl:template match="animals">
        <xsl:comment xml:lang="DE">A collection of numbers.</xsl:comment>
        <numbers>
        <xsl:for-each select="animal">
            <number><xsl:value-of select="age"/></number>
        </xsl:for-each>
        </numbers>
    </xsl:template>

</xsl:stylesheet>