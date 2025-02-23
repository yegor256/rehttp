<?xml version="1.0"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
