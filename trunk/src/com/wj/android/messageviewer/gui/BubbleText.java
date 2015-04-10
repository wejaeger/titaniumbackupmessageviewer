/*
 * $Id$
 *
 * File:   BubbleText.java
 * Author: Werner Jaeger
 *
 * Created on Mar 6, 2015, 9:16:30 AM
 *
 * Copyright (C) 2015 Werner Jaeger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.wj.android.messageviewer.gui;

import com.wj.android.messageviewer.resources.Resources;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JEditorPane;

/**
 * Used in {@link MessagePanel} to display message texts.
 *
 * @author Werner Jaeger
 */
final class BubbleText extends JEditorPane
{
   private static final long serialVersionUID = -4098589709524169577L;

   private static final String SKYPEPATTERN = "<ss.+?</ss>";

   private static final String SMILE = "\\:\\)|\\:\\=\\)|\\:\\-\\)";
   private static final String SAD = "\\:\\(|\\:\\=\\(|\\:\\-\\(";
   private static final String LAUGH = "\\:D|\\:\\=D|\\:\\-D|\\:d|\\:\\=d|\\:\\-d";
   private static final String COOL = "8\\=\\)|8\\-\\)|B\\=\\)|B\\-\\)";
   private static final String WINK = "\\;\\)|\\;\\-\\)|\\;\\=\\)";
   private static final String SURPRISED = "\\:o|\\:\\=o|\\:\\-o|\\:O|\\:\\=O|\\:\\-O";
   private static final String CRYING = "\\;\\(|\\;\\-\\(|\\;\\=\\(";
   private static final String SWEATING = "\\(\\:\\|";
   private static final String SPEECHLESS = "\\:\\||\\:\\=\\||\\:\\-\\|";
   private static final String KISS = "\\:\\*|\\:\\=\\*|\\:\\-\\*";
   private static final String CHEEKY = "\\:P|\\:\\=P|\\:\\-P|\\:p|\\:\\=p|\\:\\-p";
   private static final String BLUSH = "\\:\\$|\\:\\-\\$|\\:\\=\\$|\\:\"\\>";
   private static final String WONDERING = "\\:\\^\\)";
   private static final String SLEEPY = "\\|\\-\\)|I\\-\\)|I\\=\\)";
   private static final String DULL = "\\|\\(|\\|\\-\\(|\\|\\=\\(";
   private static final String INLOVE = "\\:\\]|\\:\\-\\]";
   private static final String EVILGRIN = "\\]\\:\\)|\\>\\:\\)";
   private static final String PUKE = "\\:\\&|\\:\\-\\&|\\:\\=\\&";
   private static final String ANGRY = "\\:\\@|\\:\\-\\@|\\:\\=\\@|x\\(|x\\-\\(|x\\=\\(|X\\(|X\\-\\(|X\\=\\(";
   private static final String WORRIED = "//:S|//://-S|//://=S|//:s|//://-s|//://=s";
   private static final String LIPSSEALED = "\\:x|\\:\\-x|\\:\\=x|\\:X|\\:\\-X|\\:\\=X|\\:\\#|\\:\\-\\#|\\:\\=\\#";
   private static final String THINKING = "\\:\\?|\\:\\-\\?|\\:\\=\\?";
//   private static final String HEART = "\\<3";
   private static final String DANCING = "\\\\o/|\\\\\\:D/|\\\\\\:d/";
   private static final String PUNCH = "\\*\\||\\*\\-\\|";
//   private static final String CAT = "\\:3";

   private static final String SMILEYPATTERNS = SMILE + "|" + SAD + "|" + LAUGH
           + "|" + COOL + "|" + WINK + "|" + SURPRISED + "|" + CRYING + "|"
           + SWEATING + "|" + SPEECHLESS + "|" + KISS + "|" + CHEEKY + "|"
           + BLUSH + "|" + WONDERING + "|" + SLEEPY + "|" + DULL + "|"
           + INLOVE + "|" + EVILGRIN + "|" + PUKE + "|" + ANGRY + "|"
           + WORRIED + "|" + LIPSSEALED + "|" + THINKING + "|" /*+ HEART+ "|" */
           + DANCING + "|" + PUNCH /* + "|" + CAT*/;
   private static final String ENTITYPATTERNS = "\\&amp\\;|\\&quot\\;|\\&lt\\;|\\&gt\\;|\\&apos\\;";
   private static final String NEWLINEPATTERN = "\n";
   private static final Pattern REPLACEPATTERNS = Pattern.compile("(" + SKYPEPATTERN + "|" + SMILEYPATTERNS + "|" + ENTITYPATTERNS + "|" + NEWLINEPATTERN + ")");
   private static final Matcher REPLACEMATCHER = REPLACEPATTERNS.matcher("");

   private static final Map<String, String> REPLACEMENTS;

   private final HtmlEditorKit m_EditorKit;

   static
   {
      REPLACEMENTS = new LinkedHashMap<>();

      // Smile
      REPLACEMENTS.put("<ss type=\"smile\">:)</ss>", "<img src='" + Resources.class.getResource("emoticon-0100-smile.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"smile\">:=)</ss>", "<img src='" + Resources.class.getResource("emoticon-0100-smile.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"smile\">:-)</ss>", "<img src='" + Resources.class.getResource("emoticon-0100-smile.gif") + "'/>");

      // Sad
      REPLACEMENTS.put("<ss type=\"sad\">:(</ss>", "<img src='" + Resources.class.getResource("emoticon-0101-sadsmile.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"sad\">:=(</ss>", "<img src='" + Resources.class.getResource("emoticon-0101-sadsmile.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"sad\">:-(</ss>", "<img src='" + Resources.class.getResource("emoticon-0101-sadsmile.gif") + "'/>");

      // Laugh
      REPLACEMENTS.put("<ss type=\"laugh\">:D</ss>", "<img src='" + Resources.class.getResource("emoticon-0102-bigsmile.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"laugh\">:=D</ss>", "<img src='" + Resources.class.getResource("emoticon-0102-bigsmile.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"laugh\">:-D</ss>", "<img src='" + Resources.class.getResource("emoticon-0102-bigsmile.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"laugh\">:d</ss>", "<img src='" + Resources.class.getResource("emoticon-0102-bigsmile.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"laugh\">:=d</ss>", "<img src='" + Resources.class.getResource("emoticon-0102-bigsmile.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"laugh\">:-d</ss>", "<img src='" + Resources.class.getResource("emoticon-0102-bigsmile.gif") + "'/>");

      // Cool
      REPLACEMENTS.put("<ss type=\"cool\">(cool)</ss>", "<img src='" + Resources.class.getResource("emoticon-0103-cool.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"cool\">(8=))</ss>", "<img src='" + Resources.class.getResource("emoticon-0103-cool.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"cool\">(8-))</ss>", "<img src='" + Resources.class.getResource("emoticon-0103-cool.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"cool\">(B=))</ss>", "<img src='" + Resources.class.getResource("emoticon-0103-cool.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"cool\">(B-))</ss>", "<img src='" + Resources.class.getResource("emoticon-0103-cool.gif") + "'/>");

      // Wink
      REPLACEMENTS.put("<ss type=\"wink\">;)</ss>", "<img src='" + Resources.class.getResource("emoticon-0105-wink.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"wink\">;-)</ss>", "<img src='" + Resources.class.getResource("emoticon-0105-wink.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"wink\">;=)</ss>", "<img src='" + Resources.class.getResource("emoticon-0105-wink.gif") + "'/>");

      // Surprised
      REPLACEMENTS.put("<ss type=\"surprised\">:o</ss>", "<img src='" + Resources.class.getResource("emoticon-0021-surprised.png") + "'/>");
      REPLACEMENTS.put("<ss type=\"surprised\">:=o</ss>", "<img src='" + Resources.class.getResource("emoticon-0021-surprised.png") + "'/>");
      REPLACEMENTS.put("<ss type=\"surprised\">:-o</ss>", "<img src='" + Resources.class.getResource("emoticon-0021-surprised.png") + "'/>");
      REPLACEMENTS.put("<ss type=\"surprised\">:O</ss>", "<img src='" + Resources.class.getResource("emoticon-0021-surprised.png") + "'/>");
      REPLACEMENTS.put("<ss type=\"surprised\">:=O</ss>", "<img src='" + Resources.class.getResource("emoticon-0021-surprised.png") + "'/>");
      REPLACEMENTS.put("<ss type=\"surprised\">:-O</ss>", "<img src='" + Resources.class.getResource("emoticon-0021-surprised.png") + "'/>");

      // Crying
      REPLACEMENTS.put("<ss type=\"cry\">(cry)</ss>", "<img src='" + Resources.class.getResource("emoticon-0106-crying.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"cry\">;(</ss>", "<img src='" + Resources.class.getResource("emoticon-0106-crying.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"cry\">;-(</ss>", "<img src='" + Resources.class.getResource("emoticon-0106-crying.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"cry\">;=(</ss>", "<img src='" + Resources.class.getResource("emoticon-0106-crying.gif") + "'/>");

      // Sweating
      REPLACEMENTS.put("<ss type=\"sweat\">(sweat)</ss>", "<img src='" + Resources.class.getResource("emoticon-0107-sweating.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"sweat\">(:|</ss>", "<img src='" + Resources.class.getResource("emoticon-0107-sweating.gif") + "'/>");

      // Speechless
      REPLACEMENTS.put("<ss type=\"speechless\">(speechless)</ss>", "<img src='" + Resources.class.getResource("emoticon-0108-speechless.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"speechless\">:|</ss>", "<img src='" + Resources.class.getResource("emoticon-0108-speechless.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"speechless\">:=|</ss>", "<img src='" + Resources.class.getResource("emoticon-0108-speechless.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"speechless\">:-|</ss>", "<img src='" + Resources.class.getResource("emoticon-0108-speechless.gif") + "'/>");

      // Kiss
      REPLACEMENTS.put("<ss type=\"kiss\">(kiss)</ss>", "<img src='" + Resources.class.getResource("emoticon-0109-kiss.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"kiss\">(xo)</ss>", "<img src='" + Resources.class.getResource("emoticon-0109-kiss.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"kiss\">:*</ss>", "<img src='" + Resources.class.getResource("emoticon-0109-kiss.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"kiss\">:=*</ss>", "<img src='" + Resources.class.getResource("emoticon-0109-kiss.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"kiss\">:-*</ss>", "<img src='" + Resources.class.getResource("emoticon-0109-kiss.gif") + "'/>");

      // Cheeky
      REPLACEMENTS.put("<ss type=\"tongueout\">(tongueout)</ss>", "<img src='" + Resources.class.getResource("emoticon-0110-tongueout.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"tongueout\">:P</ss>", "<img src='" + Resources.class.getResource("emoticon-0110-tongueout.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"tongueout\">:=P</ss>", "<img src='" + Resources.class.getResource("emoticon-0110-tongueout.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"tongueout\">:-P</ss>", "<img src='" + Resources.class.getResource("emoticon-0110-tongueout.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"tongueout\">:p</ss>", "<img src='" + Resources.class.getResource("emoticon-0110-tongueout.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"tongueout\">:=p</ss>", "<img src='" + Resources.class.getResource("emoticon-0110-tongueout.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"tongueout\">:-p</ss>", "<img src='" + Resources.class.getResource("emoticon-0110-tongueout.gif") + "'/>");

      // Fingers crossed
      REPLACEMENTS.put("<ss type=\"fingerscrossed\">(fingerscrossed)</ss>", "<img src='" + Resources.class.getResource("emoticon-0020-fingerscrossed.png") + "'/>");
      REPLACEMENTS.put("<ss type=\"fingerscrossed\">(yn)</ss>", "<img src='" + Resources.class.getResource("emoticon-0020-fingerscrossed.png") + "'/>");
      REPLACEMENTS.put("<ss type=\"fingerscrossed\">(fingers)</ss>", "<img src='" + Resources.class.getResource("emoticon-0020-fingerscrossed.png") + "'/>");
      REPLACEMENTS.put("<ss type=\"fingerscrossed\">(crossedfingers)</ss>", "<img src='" + Resources.class.getResource("emoticon-0020-fingerscrossed.png") + "'/>");

      // Blush
      REPLACEMENTS.put("<ss type=\"blush\">(blush)</ss>", "<img src='" + Resources.class.getResource("emoticon-0111-blush.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"blush\">:$</ss>", "<img src='" + Resources.class.getResource("emoticon-0111-blush.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"blush\">:-$</ss>", "<img src='" + Resources.class.getResource("emoticon-0111-blush.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"blush\">:=$</ss>", "<img src='" + Resources.class.getResource("emoticon-0111-blush.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"blush\">:&quot;&gt;</ss>", "<img src='" + Resources.class.getResource("emoticon-0111-blush.gif") + "'/>");

      // Wondering
      REPLACEMENTS.put("<ss type=\"wonder\">(wonder)</ss>", "<img src='" + Resources.class.getResource("emoticon-0112-wondering.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"wonder\">:^)</ss>", "<img src='" + Resources.class.getResource("emoticon-0112-wondering.gif") + "'/>");

      // Sleepy
      REPLACEMENTS.put("<ss type=\"sleepy\">|-)</ss>", "<img src='" + Resources.class.getResource("emoticon-0113-sleepy.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"sleepy\">I-)</ss>", "<img src='" + Resources.class.getResource("emoticon-0113-sleepy.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"sleepy\">I=)</ss>", "<img src='" + Resources.class.getResource("emoticon-0113-sleepy.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"sleepy\">(snooze)</ss>", "<img src='" + Resources.class.getResource("emoticon-0113-sleepy.gif") + "'/>");

      // Dull
      REPLACEMENTS.put("<ss type=\"dull\">(dull)(</ss>", "<img src='" + Resources.class.getResource("emoticon-0114-dull.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"dull\">|(</ss>", "<img src='" + Resources.class.getResource("emoticon-0114-dull.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"dull\">|-(</ss>", "<img src='" + Resources.class.getResource("emoticon-0114-dull.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"dull\">|=(</ss>", "<img src='" + Resources.class.getResource("emoticon-0114-dull.gif") + "'/>");

      // In love
      REPLACEMENTS.put("<ss type=\"inlove\">(inlove)</ss>", "<img src='" + Resources.class.getResource("emoticon-0115-inlove.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"inlove\">(love)</ss>", "<img src='" + Resources.class.getResource("emoticon-0115-inlove.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"inlove\">:]</ss>", "<img src='" + Resources.class.getResource("emoticon-0115-inlove.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"inlove\">:-]</ss>", "<img src='" + Resources.class.getResource("emoticon-0115-inlove.gif") + "'/>");

      // Evil grin
      REPLACEMENTS.put("<ss type=\"eg\">]:)</ss>", "<img src='" + Resources.class.getResource("emoticon-0116-evilgrin.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"eg\">%gt;:)</ss>", "<img src='" + Resources.class.getResource("emoticon-0116-evilgrin.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"eg\">(grin)</ss>", "<img src='" + Resources.class.getResource("emoticon-0116-evilgrin.gif") + "'/>");

      // Yawn
      REPLACEMENTS.put("<ss type=\"yawn\">(yawn)</ss>", "<img src='" + Resources.class.getResource("emoticon-0118-yawn.gif") + "'/>");

      // Puke
      REPLACEMENTS.put("<ss type=\"puke\">(puke)</ss>", "<img src='" + Resources.class.getResource("emoticon-0119-puke.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"puke\">:&</ss>", "<img src='" + Resources.class.getResource("emoticon-0119-puke.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"puke\">:-&</ss>", "<img src='" + Resources.class.getResource("emoticon-0119-puke.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"puke\">:=&</ss>", "<img src='" + Resources.class.getResource("emoticon-0119-puke.gif") + "'/>");

      // Doh!
      REPLACEMENTS.put("<ss type=\"doh\">(doh)</ss>", "<img src='" + Resources.class.getResource("emoticon-0120-doh.gif") + "'/>");

      // Angry
      REPLACEMENTS.put("<ss type=\"angry\">:@</ss>", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"angry\">:-@</ss>", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"angry\">:=@</ss>", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"angry\">x(</ss>", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"angry\">x-(</ss>", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"angry\">x=(</ss>", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"angry\">X(</ss>", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"angry\">X-(</ss>", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"angry\">X=(</ss>", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");

      //It wasn't me
      REPLACEMENTS.put("<ss type=\"wasntme\">(wasntme)</ss>", "<img src='" + Resources.class.getResource("emoticon-0122-itwasntme.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"wasntme\">(wm)</ss>", "<img src='" + Resources.class.getResource("emoticon-0122-itwasntme.gif") + "'/>");

      // Party!!!
      REPLACEMENTS.put("<ss type=\"party\">(party)</ss>", "<img src='" + Resources.class.getResource("emoticon-0123-party.gif") + "'/>");

      // Facepalm
      REPLACEMENTS.put("<ss type=\"facepalm\">(facepalm)</ss>", "<img src='" + Resources.class.getResource("emoticon-022-facepalm.png") + "'/>");
      REPLACEMENTS.put("<ss type=\"facepalm\">(fail)</ss>", "<img src='" + Resources.class.getResource("emoticon-022-facepalm.png") + "'/>");

      // Worried
      REPLACEMENTS.put("<ss type=\"worry\">(worry)</ss>", "<img src='" + Resources.class.getResource("emoticon-0124-worried.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"worry\">:S</ss>", "<img src='" + Resources.class.getResource("emoticon-0124-worried.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"worry\">:-S</ss>", "<img src='" + Resources.class.getResource("emoticon-0124-worried.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"worry\">:=S</ss>", "<img src='" + Resources.class.getResource("emoticon-0124-worried.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"worry\">:s</ss>", "<img src='" + Resources.class.getResource("emoticon-0124-worried.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"worry\">:-s</ss>", "<img src='" + Resources.class.getResource("emoticon-0124-worried.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"worry\">:=s</ss>", "<img src='" + Resources.class.getResource("emoticon-0124-worried.gif") + "'/>");

      // Mmm...
      REPLACEMENTS.put("<ss type=\"mmm\">(mm)</ss>", "<img src='" + Resources.class.getResource("emoticon-0125-mmm.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"mmm\">(mmm)</ss>", "<img src='" + Resources.class.getResource("emoticon-0125-mmm.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"mmm\">(mmmm)</ss>", "<img src='" + Resources.class.getResource("emoticon-0125-mmm.gif") + "'/>");

      // Lips Sealed
      REPLACEMENTS.put("<ss type=\"lipssealed\">:x</ss>", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"lipssealed\">:-x</ss>", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"lipssealed\">:=x</ss>", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"lipssealed\">:X</ss>", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"lipssealed\">:-X</ss>", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"lipssealed\">:=X</ss>", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"lipssealed\">:#</ss>", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"lipssealed\">:-#</ss>", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"lipssealed\">:=#</ss>", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");

      // Hi
      REPLACEMENTS.put("<ss type=\"hi\">(wave)</ss>", "<img src='" + Resources.class.getResource("emoticon-0128-hi.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"hi\">(hi)</ss>", "<img src='" + Resources.class.getResource("emoticon-0128-hi.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"hi\">(bye)</ss>", "<img src='" + Resources.class.getResource("emoticon-0128-hi.gif") + "'/>");

      // Devil
      REPLACEMENTS.put("<ss type=\"devil\">(devil)</ss>", "<img src='" + Resources.class.getResource("emoticon-0130-devil.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"devil\">(6)</ss>", "<img src='" + Resources.class.getResource("emoticon-0130-devil.gif") + "'/>");

      // Angel
      REPLACEMENTS.put("<ss type=\"angel\">(angel)</ss>", "<img src='" + Resources.class.getResource("emoticon-0131-angel.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"angel\">(A)</ss>", "<img src='" + Resources.class.getResource("emoticon-0131-angel.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"angel\">(a)</ss>", "<img src='" + Resources.class.getResource("emoticon-0131-angel.gif") + "'/>");

      // Envy
      REPLACEMENTS.put("<ss type=\"envy\">(envy)</ss>", "<img src='" + Resources.class.getResource("emoticon-0132-envy.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"envy\">(V)</ss>", "<img src='" + Resources.class.getResource("emoticon-0132-envy.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"envy\">(v)</ss>", "<img src='" + Resources.class.getResource("emoticon-0132-envy.gif") + "'/>");

      // Wait
      REPLACEMENTS.put("<ss type=\"wait\">(wait)</ss>", "<img src='" + Resources.class.getResource("emoticon-0133-wait.gif") + "'/>");

      // Bear-hug
      REPLACEMENTS.put("<ss type=\"hug\">(bear)</ss>", "<img src='" + Resources.class.getResource("emoticon-0134-bear.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"hug\">(hug)</ss>", "<img src='" + Resources.class.getResource("emoticon-0134-bear.gif") + "'/>");

      //Make-up
      REPLACEMENTS.put("<ss type=\"makeup\">(makeup)</ss>", "<img src='" + Resources.class.getResource("emoticon-0135-makeup.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"makeup\">(kate)</ss>", "<img src='" + Resources.class.getResource("emoticon-0135-makeup.gif") + "'/>");

      // Giggle
      REPLACEMENTS.put("<ss type=\"giggle\">(giggle)</ss>", "<img src='" + Resources.class.getResource("emoticon-0136-giggle.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"giggle\">(chuckle)</ss>", "<img src='" + Resources.class.getResource("emoticon-0136-giggle.gif") + "'/>");

      // Clapping
      REPLACEMENTS.put("<ss type=\"clap\">(clap)</ss>", "<img src='" + Resources.class.getResource("emoticon-0137-clapping.gif") + "'/>");

      // Thinking
      REPLACEMENTS.put("<ss type=\"think\">(think)</ss>", "<img src='" + Resources.class.getResource("emoticon-0138-thinking.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"think\">:?</ss>", "<img src='" + Resources.class.getResource("emoticon-0138-thinking.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"think\">:-?</ss>", "<img src='" + Resources.class.getResource("emoticon-0138-thinking.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"think\">:=?</ss>", "<img src='" + Resources.class.getResource("emoticon-0138-thinking.gif") + "'/>");

      // Bowing
      REPLACEMENTS.put("<ss type=\"bow\">(bow)</ss>", "<img src='" + Resources.class.getResource("emoticon-0139-bow.gif") + "'/>");

      // Rolling on the floor laughing
      REPLACEMENTS.put("<ss type=\"rofl\">(rofl)</ss>", "<img src='" + Resources.class.getResource("emoticon-0140-rofl.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"rofl\">(rotfl)</ss>", "<img src='" + Resources.class.getResource("emoticon-0140-rofl.gif") + "'/>");

      // Relieved
      REPLACEMENTS.put("<ss type=\"whew\">(whew)</ss>", "<img src='" + Resources.class.getResource("emoticon-0141-whew.gif") + "'/>");

      // Happy
      REPLACEMENTS.put("<ss type=\"happy\">(happy)</ss>", "<img src='" + Resources.class.getResource("emoticon-0142-happy.gif") + "'/>");

      // Smirking
      REPLACEMENTS.put("<ss type=\"smirk\">(smirk)</ss>", "<img src='" + Resources.class.getResource("emoticon-0143-smirk.gif") + "'/>");

      // Nodding
      REPLACEMENTS.put("<ss type=\"nod\">(nod)</ss>", "<img src='" + Resources.class.getResource("emoticon-0144-nod.gif") + "'/>");

      // Shaking
      REPLACEMENTS.put("<ss type=\"shake\">(shake)</ss>", "<img src='" + Resources.class.getResource("emoticon-0145-shake.gif") + "'/>");

      // Emo
      REPLACEMENTS.put("<ss type=\"emo\">(emo)</ss>", "<img src='" + Resources.class.getResource("emoticon-0147-emo.gif") + "'/>");

      // Yes
      REPLACEMENTS.put("<ss type=\"yes\">(yes)</ss>", "<img src='" + Resources.class.getResource("emoticon-0148-yes.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"yes\">(y)</ss>", "<img src='" + Resources.class.getResource("emoticon-0148-yes.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"yes\">(Y)</ss>", "<img src='" + Resources.class.getResource("emoticon-0148-yes.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"yes\">(ok)</ss>", "<img src='" + Resources.class.getResource("emoticon-0148-yes.gif") + "'/>");

      // No
      REPLACEMENTS.put("<ss type=\"no\">(no)</ss>", "<img src='" + Resources.class.getResource("emoticon-0149-no.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"no\">(n)</ss>", "<img src='" + Resources.class.getResource("emoticon-0149-no.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"no\">(N)</ss>", "<img src='" + Resources.class.getResource("emoticon-0149-no.gif") + "'/>");

      // Shaking Hands
      REPLACEMENTS.put("<ss type=\"handshake\">(handshake)</ss>", "<img src='" + Resources.class.getResource("emoticon-0150-handshake.gif") + "'/>");

      // Heart
      REPLACEMENTS.put("<ss type=\"heart\">(heart)</ss>", "<img src='" + Resources.class.getResource("emoticon-0152-heart.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"heart\">(h)</ss>", "<img src='" + Resources.class.getResource("emoticon-0152-heart.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"heart\">(H)</ss>", "<img src='" + Resources.class.getResource("emoticon-0152-heart.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"heart\">(l)</ss>", "<img src='" + Resources.class.getResource("emoticon-0152-heart.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"heart\">(L)</ss>", "<img src='" + Resources.class.getResource("emoticon-0152-heart.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"heart\">&lt;3</ss>", "<img src='" + Resources.class.getResource("emoticon-0152-heart.gif") + "'/>");

      // TMI
      REPLACEMENTS.put("<ss type=\"tmi\">(tmi)</ss>", "<img src='" + Resources.class.getResource("emoticon-0184-tmi.gif") + "'/>");

      // Heidy
      REPLACEMENTS.put("<ss type=\"heidy\">(heidy)</ss>", "<img src='" + Resources.class.getResource("emoticon-0185-heidy.gif") + "'/>");

      // Flower
      REPLACEMENTS.put("<ss type=\"flower\">(flower)</ss>", "<img src='" + Resources.class.getResource("emoticon-0155-flower.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"flower\">(F)</ss>", "<img src='" + Resources.class.getResource("emoticon-0155-flower.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"flower\">(f)</ss>", "<img src='" + Resources.class.getResource("emoticon-0155-flower.gif") + "'/>");

      // Rain
      REPLACEMENTS.put("<ss type=\"rain\">(rain)</ss>", "<img src='" + Resources.class.getResource("emoticon-0156-rain.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"rain\">(london)</ss>", "<img src='" + Resources.class.getResource("emoticon-0156-rain.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"rain\">(st)</ss>", "<img src='" + Resources.class.getResource("emoticon-0156-rain.gif") + "'/>");

      // Sun
      REPLACEMENTS.put("<ss type=\"sun\">(sun)</ss>", "<img src='" + Resources.class.getResource("emoticon-0157-sun.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"sun\">(#)</ss>", "<img src='" + Resources.class.getResource("emoticon-0157-sun.gif") + "'/>");

      // Music
      REPLACEMENTS.put("<ss type=\"music\">(music)</ss>", "<img src='" + Resources.class.getResource("emoticon-0159-music.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"music\">(8)</ss>", "<img src='" + Resources.class.getResource("emoticon-0159-music.gif") + "'/>");

      // Coffee
      REPLACEMENTS.put("<ss type=\"coffee\">(coffee)</ss>", "<img src='" + Resources.class.getResource("emoticon-0162-coffee.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"coffee\">(c)</ss>", "<img src='" + Resources.class.getResource("emoticon-0162-coffee.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"coffee\">(C)</ss>", "<img src='" + Resources.class.getResource("emoticon-0162-coffee.gif") + "'/>");

      // Pizza
      REPLACEMENTS.put("<ss type=\"pizza\">(pizza)</ss>", "<img src='" + Resources.class.getResource("emoticon-0163-pizza.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"pizza\">(pi)</ss>", "<img src='" + Resources.class.getResource("emoticon-0163-pizza.gif") + "'/>");

      // Cash
      REPLACEMENTS.put("<ss type=\"cash\">(cash)</ss>", "<img src='" + Resources.class.getResource("emoticon-0164-cash.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"cash\">(mo)</ss>", "<img src='" + Resources.class.getResource("emoticon-0164-cash.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"cash\">($)</ss>", "<img src='" + Resources.class.getResource("emoticon-0164-cash.gif") + "'/>");

      // Muscle
      REPLACEMENTS.put("<ss type=\"muscle\">(muscle)</ss>", "<img src='" + Resources.class.getResource("emoticon-0165-muscle.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"muscle\">(flex)</ss>", "<img src='" + Resources.class.getResource("emoticon-0165-muscle.gif") + "'/>");

      // Cake
      REPLACEMENTS.put("<ss type=\"cake\">(^)</ss>", "<img src='" + Resources.class.getResource("emoticon-0166-cake.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"cake\">(cake)</ss>", "<img src='" + Resources.class.getResource("emoticon-0166-cake.gif") + "'/>");

      // Beer
      REPLACEMENTS.put("<ss type=\"beer\">(beer)</ss>", "<img src='" + Resources.class.getResource("emoticon-0167-beer.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"beer\">(bricklayers)</ss>", "<img src='" + Resources.class.getResource("emoticon-0167-beer.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"beer\">(b)</ss>", "<img src='" + Resources.class.getResource("emoticon-0167-beer.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"beer\">(B)</ss>", "<img src='" + Resources.class.getResource("emoticon-0167-beer.gif") + "'/>");

      // Drink
      REPLACEMENTS.put("<ss type=\"drink\">(drink)</ss>", "<img src='" + Resources.class.getResource("emoticon-0168-drink.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"drink\">(d)</ss>", "<img src='" + Resources.class.getResource("emoticon-0168-drink.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"drink\">(D)</ss>", "<img src='" + Resources.class.getResource("emoticon-0168-drink.gif") + "'/>");

      // Dancing
      REPLACEMENTS.put("<ss type=\"dance\">(dance)</ss>", "<img src='" + Resources.class.getResource("emoticon-0169-dance.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"dance\">\\o/</ss>", "<img src='" + Resources.class.getResource("emoticon-0169-dance.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"dance\">\\:D/</ss>", "<img src='" + Resources.class.getResource("emoticon-0169-dance.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"dance\">\\:d/</ss>", "<img src='" + Resources.class.getResource("emoticon-0169-dance.gif") + "'/>");

      // Ninja
      REPLACEMENTS.put("<ss type=\"ninja\">(ninja)</ss>", "<img src='" + Resources.class.getResource("emoticon-0170-ninja.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"ninja\">(J)</ss>", "<img src='" + Resources.class.getResource("emoticon-0170-ninja.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"ninja\">(J)</ss>", "<img src='" + Resources.class.getResource("emoticon-0170-ninja.gif") + "'/>");

      // Star
      REPLACEMENTS.put("<ss type=\"star\">(star)</ss>", "<img src='" + Resources.class.getResource("emoticon-0171-star.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"star\">(*)</ss>", "<img src='" + Resources.class.getResource("emoticon-0171-star.gif") + "'/>");

      // Tumbleweed
      REPLACEMENTS.put("<ss type=\"tumbleweed\">(tumbleweed)</ss>", "<img src='" + Resources.class.getResource("emoticon-023-tumbleweed.png") + "'/>");

      // Bandit
      REPLACEMENTS.put("<ss type=\"bandit\">(bandit)</ss>", "<img src='" + Resources.class.getResource("emoticon-0174-bandit.gif") + "'/>");

      // Skype
      REPLACEMENTS.put("<ss type=\"skype\">(skype)</ss>", "<img src='" + Resources.class.getResource("emoticon-0151-skype.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"skype\">(ss)</ss>", "<img src='" + Resources.class.getResource("emoticon-0151-skype.gif") + "'/>");

      // Call
      REPLACEMENTS.put("<ss type=\"call\">(call)</ss>", "<img src='" + Resources.class.getResource("emoticon-0129-call.gif") + "'/>");

      // Talking
      REPLACEMENTS.put("<ss type=\"talk\">(talk)</ss>", "<img src='" + Resources.class.getResource("emoticon-0117-talking.gif") + "'/>");

      // Broken heart
      REPLACEMENTS.put("<ss type=\"brokenheart\">(brokenheart)</ss>", "<img src='" + Resources.class.getResource("emoticon-0153-brokenheart.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"brokenheart\">(u)</ss>", "<img src='" + Resources.class.getResource("emoticon-0153-brokenheart.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"brokenheart\">(U)</ss>", "<img src='" + Resources.class.getResource("emoticon-0153-brokenheart.gif") + "'/>");

      // Time
      REPLACEMENTS.put("<ss type=\"time\">(time)</ss>", "<img src='" + Resources.class.getResource("emoticon-0158-time.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"time\">(o)</ss>", "<img src='" + Resources.class.getResource("emoticon-0158-time.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"time\">(O)</ss>", "<img src='" + Resources.class.getResource("emoticon-0158-time.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"time\">(clock)</ss>", "<img src='" + Resources.class.getResource("emoticon-0158-time.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"time\">(0)</ss>", "<img src='" + Resources.class.getResource("emoticon-0158-time.gif") + "'/>");

      // Mail
      REPLACEMENTS.put("<ss type=\"mail\">(mail)</ss>", "<img src='" + Resources.class.getResource("emoticon-0154-mail.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"mail\">(e)</ss>", "<img src='" + Resources.class.getResource("emoticon-0154-mail.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"mail\">(m)</ss>", "<img src='" + Resources.class.getResource("emoticon-0154-mail.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"mail\">(E)</ss>", "<img src='" + Resources.class.getResource("emoticon-0154-mail.gif") + "'/>");

      // Movie
      REPLACEMENTS.put("<ss type=\"movie\">(movie)</ss>", "<img src='" + Resources.class.getResource("emoticon-0160-movie.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"movie\">((~))</ss>", "<img src='" + Resources.class.getResource("emoticon-0160-movie.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"movie\">(film)</ss>", "<img src='" + Resources.class.getResource("emoticon-0160-movie.gif") + "'/>");

      // Phone
      REPLACEMENTS.put("<ss type=\"phone\">(phone)</ss>", "<img src='" + Resources.class.getResource("emoticon-0161-phone.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"phone\">(mp)</ss>", "<img src='" + Resources.class.getResource("emoticon-0161-phone.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"phone\">(ph)</ss>", "<img src='" + Resources.class.getResource("emoticon-0161-phone.gif") + "'/>");

      // Drunk
      REPLACEMENTS.put("<ss type=\"drunk\">(drunk)</ss>", "<img src='" + Resources.class.getResource("emoticon-0175-drunk.gif") + "'/>");

      // Punch
      REPLACEMENTS.put("<ss type=\"punch\">(punch)</ss>", "<img src='" + Resources.class.getResource("emoticon-0146-punch.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"punch\">*|</ss>", "<img src='" + Resources.class.getResource("emoticon-0146-punch.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"punch\">*-|</ss>", "<img src='" + Resources.class.getResource("emoticon-0146-punch.gif") + "'/>");

      // Smoking
      REPLACEMENTS.put("<ss type=\"smoke\">(smoke)</ss>", "<img src='" + Resources.class.getResource("emoticon-0176-smoke.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"smoke\">(smoking)</ss>", "<img src='" + Resources.class.getResource("emoticon-0176-smoke.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"smoke\">(ci)</ss>", "<img src='" + Resources.class.getResource("emoticon-0176-smoke.gif") + "'/>");

      // Toivo
      REPLACEMENTS.put("<ss type=\"toivo\">(toivo)</ss>", "<img src='" + Resources.class.getResource("emoticon-0177-toivo.gif") + "'/>");

      // Rock
      REPLACEMENTS.put("<ss type=\"rock\">(rock)</ss>", "<img src='" + Resources.class.getResource("emoticon-0178-rock.gif") + "'/>");

      // Headbang
      REPLACEMENTS.put("<ss type=\"headbang\">(headbang)</ss>", "<img src='" + Resources.class.getResource("emoticon-0179-headbang.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"headbang\">(banghead)</ss>", "<img src='" + Resources.class.getResource("emoticon-0179-headbang.gif") + "'/>");

      // Bug
      REPLACEMENTS.put("<ss type=\"bug\">(bug)</ss>", "<img src='" + Resources.class.getResource("emoticon-0180-bug.gif") + "'/>");

      // Poolparty
      REPLACEMENTS.put("<ss type=\"poolparty\">(poolparty)</ss>", "<img src='" + Resources.class.getResource("emoticon-0182-poolparty.gif") + "'/>");
      REPLACEMENTS.put("<ss type=\"poolparty\">(hrv)</ss>", "<img src='" + Resources.class.getResource("emoticon-0182-poolparty.gif") + "'/>");

      // Talk to the hand
      REPLACEMENTS.put("<ss type=\"talktothehand\">(talktothehand)</ss>", "<img src='" + Resources.class.getResource("emoticon-0181-fubar.gif") + "'/>");

      // Idea
      REPLACEMENTS.put("<ss type=\"idea\">(idea)</ss>", "<img src='" + Resources.class.getResource("emoticon-024-idea.png") + "'/>");

      // Sheep
      REPLACEMENTS.put("<ss type=\"sheep\">(sheep)</ss>", "<img src='" + Resources.class.getResource("emoticon-025-sheep.png") + "'/>");

      // Cat
      REPLACEMENTS.put("<ss type=\"cat\">(cat)</ss>", "<img src='" + Resources.class.getResource("emoticon-026-cat.png") + "'/>");
      REPLACEMENTS.put("<ss type=\"cat\">:3</ss>", "<img src='" + Resources.class.getResource("emoticon-026-cat.png") + "'/>");

      // Bike
      REPLACEMENTS.put("<ss type=\"bike\">(bike)</ss>", "<img src='" + Resources.class.getResource("emoticon-027-bike.png") + "'/>");

      // Dog
      REPLACEMENTS.put("<ss type=\"dog\">(dog)</ss>", "<img src='" + Resources.class.getResource("emoticon-028-dog.png") + "'/>");
      /** ============================================================================================================================================================== */

      // Smile
      REPLACEMENTS.put(":)", "<img src='" + Resources.class.getResource("emoticon-0100-smile.gif") + "'/>");
      REPLACEMENTS.put(":=)", "<img src='" + Resources.class.getResource("emoticon-0100-smile.gif") + "'/>");
      REPLACEMENTS.put(":-)", "<img src='" + Resources.class.getResource("emoticon-0100-smile.gif") + "'/>");

      // Sad
      REPLACEMENTS.put(":(", "<img src='" + Resources.class.getResource("emoticon-0101-sadsmile.gif") + "'/>");
      REPLACEMENTS.put(":=(", "<img src='" + Resources.class.getResource("emoticon-0101-sadsmile.gif") + "'/>");
      REPLACEMENTS.put(":-(", "<img src='" + Resources.class.getResource("emoticon-0101-sadsmile.gif") + "'/>");

      // Laugh
      REPLACEMENTS.put(":D", "<img src='" + Resources.class.getResource("emoticon-0102-bigsmile.gif") + "'/>");
      REPLACEMENTS.put(":=D", "<img src='" + Resources.class.getResource("emoticon-0102-bigsmile.gif") + "'/>");
      REPLACEMENTS.put(":-D", "<img src='" + Resources.class.getResource("emoticon-0102-bigsmile.gif") + "'/>");
      REPLACEMENTS.put(":d", "<img src='" + Resources.class.getResource("emoticon-0102-bigsmile.gif") + "'/>");
      REPLACEMENTS.put(":=d", "<img src='" + Resources.class.getResource("emoticon-0102-bigsmile.gif") + "'/>");
      REPLACEMENTS.put(":-d", "<img src='" + Resources.class.getResource("emoticon-0102-bigsmile.gif") + "'/>");

      // Cool
      REPLACEMENTS.put("8=)", "<img src='" + Resources.class.getResource("emoticon-0103-cool.gif") + "'/>");
      REPLACEMENTS.put("8-)", "<img src='" + Resources.class.getResource("emoticon-0103-cool.gif") + "'/>");
      REPLACEMENTS.put("B=)", "<img src='" + Resources.class.getResource("emoticon-0103-cool.gif") + "'/>");
      REPLACEMENTS.put("B-)", "<img src='" + Resources.class.getResource("emoticon-0103-cool.gif") + "'/>");

      // Wink
      REPLACEMENTS.put(";)", "<img src='" + Resources.class.getResource("emoticon-0105-wink.gif") + "'/>");
      REPLACEMENTS.put(";-)", "<img src='" + Resources.class.getResource("emoticon-0105-wink.gif") + "'/>");
      REPLACEMENTS.put(";=)", "<img src='" + Resources.class.getResource("emoticon-0105-wink.gif") + "'/>");

      // Surprised
      REPLACEMENTS.put(":o", "<img src='" + Resources.class.getResource("emoticon-0021-surprised.png") + "'/>");
      REPLACEMENTS.put(":=o", "<img src='" + Resources.class.getResource("emoticon-0021-surprised.png") + "'/>");
      REPLACEMENTS.put(":-o", "<img src='" + Resources.class.getResource("emoticon-0021-surprised.png") + "'/>");
      REPLACEMENTS.put(":O", "<img src='" + Resources.class.getResource("emoticon-0021-surprised.png") + "'/>");
      REPLACEMENTS.put(":=O", "<img src='" + Resources.class.getResource("emoticon-0021-surprised.png") + "'/>");
      REPLACEMENTS.put(":-O", "<img src='" + Resources.class.getResource("emoticon-0021-surprised.png") + "'/>");

      // Crying
      REPLACEMENTS.put(";(", "<img src='" + Resources.class.getResource("emoticon-0106-crying.gif") + "'/>");
      REPLACEMENTS.put(";-(", "<img src='" + Resources.class.getResource("emoticon-0106-crying.gif") + "'/>");
      REPLACEMENTS.put(";=(", "<img src='" + Resources.class.getResource("emoticon-0106-crying.gif") + "'/>");

      // Sweating
      REPLACEMENTS.put("(:|", "<img src='" + Resources.class.getResource("emoticon-0107-sweating.gif") + "'/>");

      // Speechless
      REPLACEMENTS.put(":|", "<img src='" + Resources.class.getResource("emoticon-0108-speechless.gif") + "'/>");
      REPLACEMENTS.put(":=|", "<img src='" + Resources.class.getResource("emoticon-0108-speechless.gif") + "'/>");
      REPLACEMENTS.put(":-|", "<img src='" + Resources.class.getResource("emoticon-0108-speechless.gif") + "'/>");

      // Kiss
      REPLACEMENTS.put(":*", "<img src='" + Resources.class.getResource("emoticon-0109-kiss.gif") + "'/>");
      REPLACEMENTS.put(":=*", "<img src='" + Resources.class.getResource("emoticon-0109-kiss.gif") + "'/>");
      REPLACEMENTS.put(":-*", "<img src='" + Resources.class.getResource("emoticon-0109-kiss.gif") + "'/>");

      // Cheeky
      REPLACEMENTS.put(":P", "<img src='" + Resources.class.getResource("emoticon-0110-tongueout.gif") + "'/>");
      REPLACEMENTS.put(":=P", "<img src='" + Resources.class.getResource("emoticon-0110-tongueout.gif") + "'/>");
      REPLACEMENTS.put(":-P", "<img src='" + Resources.class.getResource("emoticon-0110-tongueout.gif") + "'/>");
      REPLACEMENTS.put(":p", "<img src='" + Resources.class.getResource("emoticon-0110-tongueout.gif") + "'/>");
      REPLACEMENTS.put(":=p", "<img src='" + Resources.class.getResource("emoticon-0110-tongueout.gif") + "'/>");
      REPLACEMENTS.put(":-p", "<img src='" + Resources.class.getResource("emoticon-0110-tongueout.gif") + "'/>");

      // Blush
      REPLACEMENTS.put(":$", "<img src='" + Resources.class.getResource("emoticon-0111-blush.gif") + "'/>");
      REPLACEMENTS.put(":-$", "<img src='" + Resources.class.getResource("emoticon-0111-blush.gif") + "'/>");
      REPLACEMENTS.put(":=$", "<img src='" + Resources.class.getResource("emoticon-0111-blush.gif") + "'/>");
      REPLACEMENTS.put(":\">", "<img src='" + Resources.class.getResource("emoticon-0111-blush.gif") + "'/>");

      // Wondering
      REPLACEMENTS.put(":^)", "<img src='" + Resources.class.getResource("emoticon-0112-wondering.gif") + "'/>");

      // Sleepy
      REPLACEMENTS.put("|-)", "<img src='" + Resources.class.getResource("emoticon-0113-sleepy.gif") + "'/>");
      REPLACEMENTS.put("I-)", "<img src='" + Resources.class.getResource("emoticon-0113-sleepy.gif") + "'/>");
      REPLACEMENTS.put("I=)", "<img src='" + Resources.class.getResource("emoticon-0113-sleepy.gif") + "'/>");

      // Dull
      REPLACEMENTS.put("|(", "<img src='" + Resources.class.getResource("emoticon-0114-dull.gif") + "'/>");
      REPLACEMENTS.put("|-(", "<img src='" + Resources.class.getResource("emoticon-0114-dull.gif") + "'/>");
      REPLACEMENTS.put("|=(", "<img src='" + Resources.class.getResource("emoticon-0114-dull.gif") + "'/>");

      // In love
      REPLACEMENTS.put(":]", "<img src='" + Resources.class.getResource("emoticon-0115-inlove.gif") + "'/>");
      REPLACEMENTS.put(":-]", "<img src='" + Resources.class.getResource("emoticon-0115-inlove.gif") + "'/>");

      // Evil grin
      REPLACEMENTS.put("]:)", "<img src='" + Resources.class.getResource("emoticon-0116-evilgrin.gif") + "'/>");
      REPLACEMENTS.put(">:)", "<img src='" + Resources.class.getResource("emoticon-0116-evilgrin.gif") + "'/>");

      // Puke
      REPLACEMENTS.put(":&", "<img src='" + Resources.class.getResource("emoticon-0119-puke.gif") + "'/>");
      REPLACEMENTS.put(":-&", "<img src='" + Resources.class.getResource("emoticon-0119-puke.gif") + "'/>");
      REPLACEMENTS.put(":=&", "<img src='" + Resources.class.getResource("emoticon-0119-puke.gif") + "'/>");

      // Angry
      REPLACEMENTS.put(":@", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");
      REPLACEMENTS.put(":-@", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");
      REPLACEMENTS.put(":=@", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");
      REPLACEMENTS.put("x(", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");
      REPLACEMENTS.put("x-(", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");
      REPLACEMENTS.put("x=(", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");
      REPLACEMENTS.put("X(", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");
      REPLACEMENTS.put("X-(", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");
      REPLACEMENTS.put("X=(", "<img src='" + Resources.class.getResource("emoticon-0121-angry.gif") + "'/>");

      // Worried
      REPLACEMENTS.put(":S", "<img src='" + Resources.class.getResource("emoticon-0124-worried.gif") + "'/>");
      REPLACEMENTS.put(":-S", "<img src='" + Resources.class.getResource("emoticon-0124-worried.gif") + "'/>");
      REPLACEMENTS.put(":=S", "<img src='" + Resources.class.getResource("emoticon-0124-worried.gif") + "'/>");
      REPLACEMENTS.put(":s", "<img src='" + Resources.class.getResource("emoticon-0124-worried.gif") + "'/>");
      REPLACEMENTS.put(":-s", "<img src='" + Resources.class.getResource("emoticon-0124-worried.gif") + "'/>");
      REPLACEMENTS.put(":=s", "<img src='" + Resources.class.getResource("emoticon-0124-worried.gif") + "'/>");

      // Lips Sealed
      REPLACEMENTS.put(":x", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");
      REPLACEMENTS.put(":-x", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");
      REPLACEMENTS.put(":=x", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");
      REPLACEMENTS.put(":X", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");
      REPLACEMENTS.put(":-X", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");
      REPLACEMENTS.put(":=X", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");
      REPLACEMENTS.put(":#", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");
      REPLACEMENTS.put(":-#", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");
      REPLACEMENTS.put(":=#", "<img src='" + Resources.class.getResource("emoticon-0127-lipssealed.gif") + "'/>");

      // Thinking
      REPLACEMENTS.put(":?", "<img src='" + Resources.class.getResource("emoticon-0138-thinking.gif") + "'/>");
      REPLACEMENTS.put(":-?", "<img src='" + Resources.class.getResource("emoticon-0138-thinking.gif") + "'/>");
      REPLACEMENTS.put(":=?", "<img src='" + Resources.class.getResource("emoticon-0138-thinking.gif") + "'/>");

      // Heart
//      REPLACEMENTS.put("<3", "<img src='" + Resources.class.getResource("emoticon-0152-heart.gif") + "'/>");

      // Dancing
      REPLACEMENTS.put("\\o/", "<img src='" + Resources.class.getResource("emoticon-0169-dance.gif") + "'/>");
      REPLACEMENTS.put("\\:D/", "<img src='" + Resources.class.getResource("emoticon-0169-dance.gif") + "'/>");
      REPLACEMENTS.put("\\:d/", "<img src='" + Resources.class.getResource("emoticon-0169-dance.gif") + "'/>");

      // Punch
      REPLACEMENTS.put("*|", "<img src='" + Resources.class.getResource("emoticon-024-punch.png") + "'/>");
      REPLACEMENTS.put("*-|", "<img src='" + Resources.class.getResource("emoticon-024-punch.png") + "'/>");

      // Cat
//      REPLACEMENTS.put(":3", "<img src='" + Resources.class.getResource("emoticon-026-cat.png") + "'/>");

      // Html entities
      REPLACEMENTS.put("&amp;", "&");
      REPLACEMENTS.put("&quot;", "\"");
      REPLACEMENTS.put("&lt;", "<");
      REPLACEMENTS.put("&gt;", ">");
      REPLACEMENTS.put("&apos;", "'");
      REPLACEMENTS.put("\n", "<br/>");
   }

   /**
    * Constructs a new {@code BubbleText}
    */
   BubbleText()
   {
      this(null);
   }

   /**
    * Constructs a new {@code BubbleText} with the specified text displayed. A
    * default model is created and rows/columns are set to 0.
    *
    * @param strText the text to be displayed, or {@code null}
    */
   BubbleText(final String strText)
   {
//      super("text/html", tohtml(strText));
      super();

      m_EditorKit = new HtmlEditorKit();
      setEditorKit(m_EditorKit);
      setContentType("text/html");
      setText(tohtml(strText));
   }

   /**
    * Sets the text of this {@code BubbleText} to the specified text.
    *
    * <p>
    *    If the text is null or empty, has the effect of simply deleting the old
    *    text.
    * </p>
    *
    * @param strText the new text to be set
    */
   @Override
   public void setText(final String strText)
   {
      super.setText(tohtml(strText));
   }

   /**
    * Returns the preferred size for the {@code BubbleText}.
    *
    * <p>
    *    The preferred size for {@code BubbleText} is slightly altered
    *    from the preferred size of the superclass. Returns  a width which is
    *    min({@link #getSize(int, int)}, {@link #super.getPreferredSize()}) and
    *    the height as returned by the the superclass.
    * </p>
    *
    * @return a {@code Dimension} containing the preferred size
    */
   @Override
   public Dimension getPreferredSize()
   {
      final Dimension d = super.getPreferredSize();
      d.width = Math.min(d.width + 20, getSize().width);
      d.height += 30;
      return(d);
   }

   private static String tohtml(final String strBody)
   {
      final StringBuffer strHtml = new StringBuffer("<html><body>");

      if (null != strBody)
      {
         REPLACEMATCHER.reset(strBody);

         while (REPLACEMATCHER.find())
         {
            final String strReplacement = REPLACEMENTS.get(REPLACEMATCHER.group(1));
            if (null != strReplacement)
               REPLACEMATCHER.appendReplacement(strHtml, strReplacement);
         }
         REPLACEMATCHER.appendTail(strHtml);
      }

      strHtml.append("</body></html>");

      return(strHtml.toString());
   }
}
