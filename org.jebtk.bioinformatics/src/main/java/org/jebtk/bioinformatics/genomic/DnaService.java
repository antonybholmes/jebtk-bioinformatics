/**
 * Copyright (C) 2016, Antony Holmes
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. Neither the name of copyright holder nor the names of its contributors 
 *     may be used to endorse or promote products derived from this software 
 *     without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jebtk.bioinformatics.genomic;

import java.awt.Color;

import org.jebtk.core.collections.DefaultHashMap;
import org.jebtk.core.collections.EntryCreator;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.event.ChangeEvent;
import org.jebtk.core.event.ChangeListener;
import org.jebtk.core.event.ChangeListeners;
import org.jebtk.core.settings.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server for genome feature annotations.
 *
 * @author Antony Holmes Holmes
 *
 */
public class DnaService extends ChangeListeners {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * The Class DnaServiceLoader.
   */
  private static class DnaServiceLoader {

    /** The Constant INSTANCE. */
    private static final DnaService INSTANCE = new DnaService();
  }

  /**
   * Gets the single instance of SettingsService.
   *
   * @return single instance of SettingsService
   */
  public static DnaService getInstance() {
    return DnaServiceLoader.INSTANCE;
  }

  /**
   * The constant LOG.
   */
  private static final Logger LOG = LoggerFactory.getLogger(DnaService.class);

  private IterMap<Character, Color> mColorMap = DefaultHashMap
      .create(SettingsService.getInstance()
          .getAsColor("bioinformatics.dna.bases.n.color"));

  private IterMap<Character, ChangeListeners> mListenerMap = DefaultHashMap
      .create(new EntryCreator<ChangeListeners>() {

        @Override
        public ChangeListeners newEntry() {
          return new ChangeListeners();
        }
      });

  /**
   * Instantiates a new cytobands.
   */
  private DnaService() {
    updateColors();
  }

  /**
   * Get a base color.
   * 
   * @param base
   * @return
   */
  public Color getBaseColor(char base) {
    return mColorMap.get(base);
  }

  /**
   * Set the color for a given base.
   * 
   * @param base
   * @param color
   */
  public void setBaseColor(char base, Color color) {
    mColorMap.put(base, color);

    // SysUtils.err().println("Set base color", base, color);

    fireChanged(base);

    fireChanged();
  }

  /**
   * Gets the base A color.
   *
   * @return the base A color
   */
  public Color getBaseAColor() {
    return getBaseColor('A');
  }

  /**
   * Sets the base A color.
   *
   * @param color the new base A color
   */
  public void setBaseAColor(Color color) {
    SettingsService.getInstance().update("bioinformatics.dna.bases.a.color",
        color);

    setBaseColor('A', color);
  }

  /**
   * Gets the base C color.
   *
   * @return the base C color
   */
  public Color getBaseCColor() {
    return getBaseColor('C');
  }

  /**
   * Sets the base C color.
   *
   * @param color the new base C color
   */
  public void setBaseCColor(Color color) {
    SettingsService.getInstance().update("bioinformatics.dna.bases.c.color",
        color);
    setBaseColor('C', color);
  }

  /**
   * Gets the base G color.
   *
   * @return the base G color
   */
  public Color getBaseGColor() {
    return getBaseColor('G');
  }

  /**
   * Sets the base G color.
   *
   * @param color the new base G color
   */
  public void setBaseGColor(Color color) {
    SettingsService.getInstance().update("bioinformatics.dna.bases.g.color",
        color);
    setBaseColor('G', color);
  }

  /**
   * Gets the base T color.
   *
   * @return the base T color
   */
  public Color getBaseTColor() {
    return getBaseColor('T');
  }

  /**
   * Sets the base T color.
   *
   * @param color the new base T color
   */
  public void setBaseTColor(Color color) {
    SettingsService.getInstance().update("bioinformatics.dna.bases.t.color",
        color);
    setBaseColor('T', color);
  }

  /**
   * Gets the base N color.
   *
   * @return the base N color
   */
  public Color getBaseNColor() {
    return getBaseColor('N');
  }

  /**
   * Sets the base N color.
   *
   * @param color the new base N color
   */
  public void setBaseNColor(Color color) {
    SettingsService.getInstance().update("bioinformatics.dna.bases.n.color",
        color);
    setBaseColor('N', color);
  }

  /**
   * Reset the colors back to their defaults.
   */
  public void reset() {
    SettingsService.getInstance()
        .resetToDefault("bioinformatics.dna.bases.a.color");
    SettingsService.getInstance()
        .resetToDefault("bioinformatics.dna.bases.c.color");
    SettingsService.getInstance()
        .resetToDefault("bioinformatics.dna.bases.g.color");
    SettingsService.getInstance()
        .resetToDefault("bioinformatics.dna.bases.t.color");
    SettingsService.getInstance()
        .resetToDefault("bioinformatics.dna.bases.n.color");

    updateColors();
  }

  /**
   * Update colors.
   */
  private void updateColors() {
    mColorMap.put('A',
        SettingsService.getInstance()
            .getAsColor("bioinformatics.dna.bases.a.color"));

    mColorMap.put('C',
        SettingsService.getInstance()
            .getAsColor("bioinformatics.dna.bases.c.color"));

    mColorMap.put('G',
        SettingsService.getInstance()
            .getAsColor("bioinformatics.dna.bases.g.color"));

    mColorMap.put('T',
        SettingsService.getInstance()
            .getAsColor("bioinformatics.dna.bases.t.color"));

    mColorMap.put('N',
        SettingsService.getInstance()
            .getAsColor("bioinformatics.dna.bases.n.color"));

    fireAllChanged();
  }

  private void fireAllChanged() {
    fireChanged('A');
    fireChanged('C');
    fireChanged('G');
    fireChanged('T');
    fireChanged('N');

    fireChanged();
  }

  /**
   * Fire that a particular base has changed.
   * 
   * @param base
   */
  private void fireChanged(char base) {
    mListenerMap.get(base).fireChanged(new ChangeEvent(this));
  }

  /**
   * Receive events for a specific base.
   * 
   * @param base
   * @param l
   */
  public void addChangeListener(char base, ChangeListener l) {
    mListenerMap.get(base).addChangeListener(l);
  }
}