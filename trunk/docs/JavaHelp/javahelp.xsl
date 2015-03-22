<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id$ -->
<!--
    Document   : javahelp.xsl
    Created on : March 21, 2015, 6:06 PM
    Author     : Werner Jäger
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

   <xsl:import href="@docbook.home@/javahelp/javahelp.xsl"/>


   <xsl:output method="html"
            encoding="utf-8"
            indent="yes"/>

   <xsl:param name="docbook.home"/>
   <xsl:param name="generate.toc">
   appendix  nop
   article/appendix  nop
   article   nop
   book      nop
   chapter   nop
   part      nop
   preface   nop
   qandadiv  nop
   qandaset  nop
   reference nop
   sect1     nop
   sect2     nop
   sect3     nop
   sect4     nop
   sect5     nop
   section   nop
   set       nop
   </xsl:param>
   <xsl:param name="chunk.quietly" select="1"/>
   <xsl:param name="chunk.first.sections" select="1"/>
   <xsl:param name="chunk.section.depth" select="2"/>
   <xsl:param name="ulink.target" select="'_blank'"/>
   <xsl:param name="html.stylesheet" select="'javahelp.css'"/>
   <xsl:param name="admon.graphics" select="1"/>
   <xsl:param name="make.valid.html" select="1"/>
   <xsl:param name="html.cleanup" select="1"/>

</xsl:stylesheet>
