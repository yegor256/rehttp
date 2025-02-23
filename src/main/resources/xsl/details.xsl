<?xml version="1.0"?>
<!--
 * Copyright (c) 2017-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge,  to any person obtaining
 * a copy  of  this  software  and  associated  documentation files  (the
 * "Software"),  to deal in the Software  without restriction,  including
 * without limitation the rights to use,  copy,  modify,  merge, publish,
 * distribute,  sublicense,  and/or sell  copies of the Software,  and to
 * permit persons to whom the Software is furnished to do so,  subject to
 * the  following  conditions:   the  above  copyright  notice  and  this
 * permission notice  shall  be  included  in  all copies or  substantial
 * portions of the Software.  The software is provided  "as is",  without
 * warranty of any kind, express or implied, including but not limited to
 * the warranties  of merchantability,  fitness for  a particular purpose
 * and non-infringement.  In  no  event shall  the  authors  or copyright
 * holders be liable for any claim,  damages or other liability,  whether
 * in an action of contract,  tort or otherwise,  arising from, out of or
 * in connection with the software or  the  use  or other dealings in the
 * software.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="1.0">
  <xsl:output method="xml" doctype-system="about:legacy-compat" encoding="UTF-8" indent="yes"/>
  <xsl:strip-space elements="*"/>
  <xsl:include href="/xsl/layout.xsl"/>
  <xsl:template match="page" mode="head">
    <title>
      <xsl:value-of select="target/time"/>
    </title>
  </xsl:template>
  <xsl:template match="page" mode="body">
    <xsl:apply-templates select="target"/>
  </xsl:template>
  <xsl:template match="target">
    <p>
      <xsl:text>URL: </xsl:text>
      <code>
        <xsl:value-of select="url"/>
      </code>
    </p>
    <p>
      <xsl:text>Created: </xsl:text>
      <xsl:value-of select="time_utc"/>
    </p>
    <p>
      <xsl:text>Attempts: </xsl:text>
      <xsl:value-of select="attempts"/>
    </p>
    <p>
      <xsl:text>Recent HTTP code: </xsl:text>
      <code>
        <xsl:value-of select="code"/>
      </code>
    </p>
    <p>
      <xsl:text>Next attempt on: </xsl:text>
      <xsl:value-of select="when_utc"/>
    </p>
    <p>
      <xsl:text>HTTP request:</xsl:text>
    </p>
    <pre>
      <xsl:value-of select="request" disable-output-escaping="no"/>
    </pre>
    <p>
      <xsl:text>HTTP response:</xsl:text>
    </p>
    <pre>
      <xsl:value-of select="response" disable-output-escaping="no"/>
    </pre>
  </xsl:template>
</xsl:stylesheet>
