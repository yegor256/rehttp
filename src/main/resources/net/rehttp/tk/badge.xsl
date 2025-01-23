<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2017-2025 Yegor Bugayenko

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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/2000/svg" version="1.0">
  <xsl:param name="style"/>
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:template match="/info">
    <svg width="106" height="20">
      <xsl:comment>
        <xsl:text>URL: </xsl:text>
        <xsl:value-of select="url"/>
        <xsl:text>, total: </xsl:text>
        <xsl:value-of select="total"/>
        <xsl:text>, failures: </xsl:text>
        <xsl:value-of select="failures"/>
      </xsl:comment>
      <xsl:if test="$style = 'round'">
        <linearGradient id="b" x2="0" y2="100%">
          <stop offset="0" stop-color="#bbb" stop-opacity=".1"/>
          <stop offset="1" stop-opacity=".1"/>
        </linearGradient>
      </xsl:if>
      <mask id="a">
        <rect width="106" height="20" fill="#fff">
          <xsl:if test="$style = 'round'">
            <xsl:attribute name="rx">
              <xsl:text>3</xsl:text>
            </xsl:attribute>
          </xsl:if>
        </rect>
      </mask>
      <g mask="url(#a)">
        <path fill="#555" d="M0 0h37v20H0z"/>
        <path d="M37 0h77v20H37z">
          <xsl:attribute name="fill">
            <xsl:choose>
              <xsl:when test="number(failures) = 0">
                <xsl:text>#44cc11</xsl:text>
              </xsl:when>
              <xsl:when test="number(failures) &lt; 3">
                <xsl:text>#dfb317</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>#d9644d</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
        </path>
      </g>
      <g fill="#fff" text-anchor="middle" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" font-size="11">
        <xsl:if test="$style = 'round'">
          <path fill="#1a1a1a" fill-rule="nonzero" d="M20.246999,12.6198566 L15.7880562,14.6693293 L15.7880562,14.6713974 C14.667229,15.2008273 13.7352369,16.1645552 13.2560427,17.4467682 C12.2834409,20.0463517 13.5281275,22.9623523 16.0378057,23.9612376 C18.5474839,24.9601229 21.3718871,23.6613652 22.3444889,21.0638498 C22.9170854,19.5376026 22.718098,17.9038151 21.9526055,16.6112616 L20.246999,12.6198566 Z M20.9800037,20.5199433 C20.2977611,22.3398585 18.3221002,23.2477481 16.5657316,22.5487352 C14.8093629,21.8497223 13.9382853,19.8085219 14.6184974,17.9886066 C14.868247,17.320615 15.2946486,16.7746404 15.8205439,16.3879084 L19.5606954,14.5431761 L21.13229,18.4973556 C21.2703629,19.1488026 21.2317837,19.8498836 20.9800037,20.5199433 L20.9800037,20.5199433 Z M20.5414192,-4.14818087 C18.031741,-5.14706618 15.2073378,-3.85037655 14.234736,-1.25079305 C13.6621395,0.27545407 13.8611269,1.90924163 14.6266194,3.20179509 L16.3301954,7.19113206 L20.7891382,5.1437274 L20.7891382,5.14165931 C21.9099654,4.61222942 22.8419575,3.64850156 23.3231822,2.36628854 C24.295784,-0.233294969 23.0510974,-3.14929556 20.5414192,-4.14818087 Z M21.958697,1.82238204 C21.7089475,2.49244175 21.2825458,3.03634825 20.7566505,3.42308024 L17.016499,5.26781253 L15.4449044,1.31570109 C15.3068315,0.662186063 15.3454107,-0.0368268443 15.5971907,-0.70895464 C16.2794333,-2.5288699 18.2550942,-3.43675945 20.0114629,-2.73774654 C21.7678315,-2.03873363 22.6409396,0.00246678063 21.958697,1.82238204 L21.958697,1.82238204 Z M21.3475213,6.25222124 L14.9393139,9.13306437 L15.5484591,10.526954 L21.9566665,7.64817897 L21.3475213,6.25222124 L21.3475213,6.25222124 Z M15.2296731,13.5587674 L21.6378805,10.6799923 L21.0287353,9.2861027 L14.6205279,12.1648778 L15.2296731,13.5587674 L15.2296731,13.5587674 Z" id="Shape" transform="translate(18.289612, 9.906403) rotate(75.000000) translate(-18.289612, -9.906403) "/>
        </xsl:if>
        <path fill="#ffffff" fill-rule="nonzero" d="M20.246999,12.6198566 L15.7880562,14.6693293 L15.7880562,14.6713974 C14.667229,15.2008273 13.7352369,16.1645552 13.2560427,17.4467682 C12.2834409,20.0463517 13.5281275,22.9623523 16.0378057,23.9612376 C18.5474839,24.9601229 21.3718871,23.6613652 22.3444889,21.0638498 C22.9170854,19.5376026 22.718098,17.9038151 21.9526055,16.6112616 L20.246999,12.6198566 Z M20.9800037,20.5199433 C20.2977611,22.3398585 18.3221002,23.2477481 16.5657316,22.5487352 C14.8093629,21.8497223 13.9382853,19.8085219 14.6184974,17.9886066 C14.868247,17.320615 15.2946486,16.7746404 15.8205439,16.3879084 L19.5606954,14.5431761 L21.13229,18.4973556 C21.2703629,19.1488026 21.2317837,19.8498836 20.9800037,20.5199433 L20.9800037,20.5199433 Z M20.5414192,-4.14818087 C18.031741,-5.14706618 15.2073378,-3.85037655 14.234736,-1.25079305 C13.6621395,0.27545407 13.8611269,1.90924163 14.6266194,3.20179509 L16.3301954,7.19113206 L20.7891382,5.1437274 L20.7891382,5.14165931 C21.9099654,4.61222942 22.8419575,3.64850156 23.3231822,2.36628854 C24.295784,-0.233294969 23.0510974,-3.14929556 20.5414192,-4.14818087 Z M21.958697,1.82238204 C21.7089475,2.49244175 21.2825458,3.03634825 20.7566505,3.42308024 L17.016499,5.26781253 L15.4449044,1.31570109 C15.3068315,0.662186063 15.3454107,-0.0368268443 15.5971907,-0.70895464 C16.2794333,-2.5288699 18.2550942,-3.43675945 20.0114629,-2.73774654 C21.7678315,-2.03873363 22.6409396,0.00246678063 21.958697,1.82238204 L21.958697,1.82238204 Z M21.3475213,6.25222124 L14.9393139,9.13306437 L15.5484591,10.526954 L21.9566665,7.64817897 L21.3475213,6.25222124 L21.3475213,6.25222124 Z M15.2296731,13.5587674 L21.6378805,10.6799923 L21.0287353,9.2861027 L14.6205279,12.1648778 L15.2296731,13.5587674 L15.2296731,13.5587674 Z" id="Shape" transform="translate(18.289612, 9.906403) rotate(75.000000) translate(-18.289612, -9.906403) "/>
        <xsl:variable name="text">
          <xsl:choose>
            <xsl:when test="failures &lt; 100">
              <xsl:value-of select="failures"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>99+</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:text>/</xsl:text>
          <xsl:choose>
            <xsl:when test="total &lt; 1000">
              <xsl:value-of select="total"/>
            </xsl:when>
            <xsl:when test="total &lt; 1000000">
              <xsl:value-of select="format-number(total div 1000, '0')"/>
              <xsl:text>K</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>999+</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:if test="$style = 'round'">
          <text x="102.5" y="15" fill="#010101" fill-opacity=".3" text-anchor="end">
            <xsl:value-of select="$text"/>
          </text>
        </xsl:if>
        <text x="102.5" y="14" text-anchor="end">
          <xsl:value-of select="$text"/>
        </text>
      </g>
    </svg>
  </xsl:template>
</xsl:stylesheet>
