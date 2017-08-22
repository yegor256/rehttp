<?xml version="1.0"?>
<!--
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Yegor Bugayenko
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml" version="1.0">
    <xsl:output method="html" doctype-system="about:legacy-compat"
        encoding="UTF-8" indent="yes" />
    <xsl:strip-space elements="*"/>
    <xsl:include href="/xsl/layout.xsl"/>
    <xsl:template match="page" mode="head">
        <title>
            <xsl:text>rehttp</xsl:text>
        </title>
    </xsl:template>
    <xsl:template match="page" mode="body">
        <p>
            <code>
                <xsl:value-of select="url"/>
            </code>
        </p>
        <xsl:apply-templates select="targets"/>
    </xsl:template>
    <xsl:template match="targets[not(target)]">
        <p>
            <xsl:text>There are no requests yet.</xsl:text>
        </p>
    </xsl:template>
    <xsl:template match="targets">
        <table>
            <thead>
                <tr>
                    <th>
                        <xsl:text>Time</xsl:text>
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
                <xsl:apply-templates select="target"/>
            </tbody>
        </table>
    </xsl:template>
    <xsl:template match="target">
        <tr>
            <td>
                <a href="/d?u={/page/encoded_url}&amp;t={time}">
                    <xsl:value-of select="time"/>
                </a>
            </td>
            <td>
                <code>
                    <xsl:value-of select="code"/>
                </code>
            </td>
            <td>
                <xsl:value-of select="attempts"/>
            </td>
        </tr>
    </xsl:template>
</xsl:stylesheet>
