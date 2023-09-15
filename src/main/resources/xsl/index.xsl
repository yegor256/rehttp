<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2017-2022 Yegor Bugayenko

Permission is hereby granted, free of charge,  to any person obtaining
a copy  of  this  software  and  associated  documentation files  (the
"Software"),  to deal in the Software  without restriction,  including
without limitation the rights to use,  copy,  modify,  merge, publish,
distribute,  sublicense,  and/or sell  copies of the Software,  and to
permit persons to whom the Software is furnished to do so,  subject to
the  following  conditions:   the  above  copyright  notice  and  this
permission notice  shall  be  included  in  all copies or  substantial
portions of the Software.  The software is provided  "as is",  without
warranty of any kind, express or implied, including but not limited to
the warranties  of merchantability,  fitness for  a particular purpose
and non-infringement.  In  no  event shall  the  authors  or copyright
holders be liable for any claim,  damages or other liability,  whether
in an action of contract,  tort or otherwise,  arising from, out of or
in connection with the software or  the  use  or other dealings in the
software.
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
      <strong>
        <xsl:text>ReHTTP</xsl:text>
      </strong>
      <xsl:text>
        is an HTTP repeater.
        When someone is sending you HTTP requests with an
        important information, you may fail to accept them for
        some reasons. For example, an HTTP POST is coming
        from a GitHub Webhook to your server, but the server
        is temporary down. You will loose the information and
        GitHub won't retry. Our repeater helps you solve exactly
        this problem.
      </xsl:text>
    </p>
    <p>
      <xsl:text>Let's say, the URL of your server is </xsl:text>
      <code>http://www.example.com/hook</code>
      <xsl:text>. Instead of giving this URL directly to GitHub,
        give them this: </xsl:text>
      <code>https://www.rehttp.net/p/http://www.example.com/hook</code>
      <xsl:text>.</xsl:text>
      <xsl:text> GitHub will send POST or PUT requests to us,
        and we will send them through, to your server. If your
        server doesn't reply, we will try again in a minute.
        Then, we will retry in a few minutes, and in a few hours.
        Only after 24 hours of failed attempts we will abandon it.</xsl:text>
    </p>
    <form action="/i" method="get">
      <fieldset>
        <label>
          <xsl:text>To see the history of a URL:</xsl:text>
        </label>
        <input type="url" name="u" size="45"/>
        <button type="submit">
          <xsl:text>Open</xsl:text>
        </button>
      </fieldset>
    </form>
    <p>
      <xsl:text>The service is absolutely free, please don't abuse it.</xsl:text>
    </p>
  </xsl:template>
</xsl:stylesheet>
