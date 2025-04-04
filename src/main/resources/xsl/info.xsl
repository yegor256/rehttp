<?xml version="1.0" encoding="UTF-8"?>
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
      <xsl:text>ReHTTP</xsl:text>
    </title>
  </xsl:template>
  <xsl:template match="page" mode="body">
    <p>
      <img src="/b?u={/page/encoded_url}"/>
    </p>
    <p>
      <xsl:text>Status via HTTP: </xsl:text>
      <a href="/s?u={/page/encoded_url}">
        <xsl:text>here</xsl:text>
      </a>
    </p>
    <p>
      <xsl:text>URL: </xsl:text>
      <code>
        <xsl:value-of select="url"/>
      </code>
    </p>
    <xsl:apply-templates select="targets"/>
    <xsl:apply-templates select="history"/>
  </xsl:template>
  <xsl:template match="targets[target]">
    <p>
      <xsl:text>Recent </xsl:text>
      <xsl:value-of select="count(target)"/>
      <xsl:text> failures:</xsl:text>
    </p>
    <table>
      <thead>
        <tr>
          <th>
            <xsl:text>Received</xsl:text>
          </th>
          <th>
            <xsl:text>Code</xsl:text>
          </th>
          <th>
            <xsl:text>Attempts</xsl:text>
          </th>
          <th>
            <xsl:text>Retry in</xsl:text>
          </th>
          <th>
            <xsl:text>Give up in</xsl:text>
          </th>
        </tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="target" mode="failures"/>
      </tbody>
    </table>
  </xsl:template>
  <xsl:template match="history[not(target)]">
    <p>
      <xsl:text>There are no HTTP requests yet.</xsl:text>
    </p>
  </xsl:template>
  <xsl:template match="history[target]">
    <p>
      <xsl:text>Recent </xsl:text>
      <xsl:value-of select="count(target)"/>
      <xsl:text> requests:</xsl:text>
    </p>
    <table>
      <thead>
        <tr>
          <th>
            <xsl:text>Received</xsl:text>
          </th>
          <th>
            <xsl:text>Code</xsl:text>
          </th>
          <th>
            <xsl:text>Attempts</xsl:text>
          </th>
        </tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="target" mode="history"/>
      </tbody>
    </table>
  </xsl:template>
  <xsl:template match="target" mode="failures">
    <tr>
      <td>
        <a href="/d?u={/page/encoded_url}&amp;t={time}" title="{time_utc}">
          <xsl:call-template name="minutes">
            <xsl:with-param name="minutes" select="- age"/>
          </xsl:call-template>
          <xsl:text> ago</xsl:text>
        </a>
      </td>
      <td>
        <code>
          <xsl:value-of select="code"/>
        </code>
      </td>
      <td style="text-align: right;">
        <xsl:value-of select="attempts"/>
      </td>
      <td title="{when_utc}">
        <xsl:call-template name="minutes">
          <xsl:with-param name="minutes" select="minutes_left"/>
        </xsl:call-template>
      </td>
      <td title="{ttl_utc}">
        <xsl:call-template name="minutes">
          <xsl:with-param name="minutes" select="ttl_minutes_left"/>
        </xsl:call-template>
      </td>
    </tr>
  </xsl:template>
  <xsl:template match="target" mode="history">
    <tr>
      <td>
        <a href="/d?u={/page/encoded_url}&amp;t={time}" title="{time_utc}">
          <xsl:call-template name="minutes">
            <xsl:with-param name="minutes" select="- age"/>
          </xsl:call-template>
          <xsl:text> ago</xsl:text>
        </a>
      </td>
      <td>
        <code>
          <xsl:value-of select="code"/>
        </code>
      </td>
      <td style="text-align: right;">
        <xsl:value-of select="attempts"/>
      </td>
    </tr>
  </xsl:template>
  <xsl:template name="minutes">
    <xsl:param name="minutes"/>
    <xsl:choose>
      <xsl:when test="$minutes &lt; 0">
        <span title="{$minutes} minutes" style="color:red;">
          <xsl:value-of select="$minutes"/>
          <xsl:text> minutes</xsl:text>
        </span>
      </xsl:when>
      <xsl:when test="$minutes = 0">
        <xsl:text>seconds</xsl:text>
      </xsl:when>
      <xsl:when test="$minutes &lt; 60">
        <span title="{$minutes} minutes">
          <xsl:value-of select="$minutes"/>
          <xsl:text> mins</xsl:text>
        </span>
      </xsl:when>
      <xsl:when test="$minutes &lt; 24 * 60">
        <span title="{format-number($minutes div 60, '0')} hours ({$minutes} minutes)">
          <xsl:value-of select="format-number($minutes div 60, '0')"/>
          <xsl:text> hrs</xsl:text>
        </span>
      </xsl:when>
      <xsl:otherwise>
        <span title="{format-number($minutes div (60 * 24), '0')} days ({$minutes} minutes)">
          <xsl:value-of select="format-number($minutes div (60 * 24), '0')"/>
          <xsl:text> days</xsl:text>
        </span>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
