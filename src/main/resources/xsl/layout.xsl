<?xml version="1.0"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="1.0">
  <xsl:include href="/org/takes/rs/xe/sla.xsl"/>
  <xsl:include href="/org/takes/rs/xe/memory.xsl"/>
  <xsl:include href="/org/takes/rs/xe/millis.xsl"/>
  <xsl:include href="/org/takes/facets/flash/flash.xsl"/>
  <xsl:template match="/page">
    <html lang="en">
      <head>
        <meta charset="UTF-8"/>
        <meta name="viewport" content="width=device-width,minimum-scale=1,initial-scale=1"/>
        <link rel="shortcut icon" href="/images/logo.png"/>
        <link rel="stylesheet" href="//yegor256.github.io/tacit/tacit.min.css"/>
        <link rel="stylesheet" href="/css/main.css"/>
        <xsl:apply-templates select="." mode="head"/>
      </head>
      <body>
        <section>
          <header>
            <nav>
              <ul>
                <li>
                  <a href="{links/link[@rel='home']/@href}">
                    <img src="/images/logo.svg" class="logo"/>
                  </a>
                </li>
              </ul>
            </nav>
            <xsl:call-template name="takes_flash">
              <xsl:with-param name="flash" select="flash"/>
            </xsl:call-template>
          </header>
          <article>
            <xsl:apply-templates select="." mode="body"/>
          </article>
          <footer>
            <nav>
              <ul style="color:gray;">
                <li title="Currently deployed version">
                  <xsl:text>v</xsl:text>
                  <xsl:value-of select="version/name"/>
                </li>
                <li title="Server time to generate this page">
                  <xsl:call-template name="takes_millis">
                    <xsl:with-param name="millis" select="millis"/>
                  </xsl:call-template>
                </li>
                <li title="Load average of the server">
                  <xsl:call-template name="takes_sla">
                    <xsl:with-param name="sla" select="@sla"/>
                  </xsl:call-template>
                </li>
                <li title="Free/total memory in Mb">
                  <xsl:call-template name="takes_memory">
                    <xsl:with-param name="memory" select="memory"/>
                  </xsl:call-template>
                </li>
                <li title="Current date/time">
                  <xsl:value-of select="@date"/>
                </li>
              </ul>
            </nav>
            <nav>
              <ul>
                <li>
                  <a href="http://www.zerocracy.com">
                    <img src="//www.0crat.com/badge/C3RFVLU72.svg"/>
                  </a>
                </li>
              </ul>
            </nav>
            <nav>
              <ul>
                <li>
                  <a href="http://www.sixnines.io/h/a19c">
                    <img src="//www.sixnines.io/b/a19c?style=flat"/>
                  </a>
                </li>
              </ul>
            </nav>
            <nav>
              <ul>
                <li>
                  <a href="https://github.com/yegor256/rehttp/stargazers">
                    <img src="//img.shields.io/github/stars/yegor256/rehttp.svg?style=flat-square" alt="GitHub stars"/>
                  </a>
                </li>
              </ul>
            </nav>
            <nav>
              <ul style="color:gray;">
                <li>
                  <xsl:text>The logo is made by </xsl:text>
                  <a href="https://www.flaticon.com/authors/epiccoders">
                    <xsl:text>EpicCoders</xsl:text>
                  </a>
                  <xsl:text>.</xsl:text>
                </li>
              </ul>
            </nav>
          </footer>
        </section>
        <script>
          (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
          (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
          m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
          })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
          ga('create', 'UA-1963507-52', 'auto');
          ga('send', 'pageview');
        </script>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
