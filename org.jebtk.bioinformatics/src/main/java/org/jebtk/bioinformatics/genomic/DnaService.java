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

import org.jebtk.core.event.ChangeListeners;
import org.jebtk.core.settings.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
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

  /** The m base A color. */
  public Color mBaseAColor;

  /** The m base C color. */
  public Color mBaseCColor;

  /** The m base G color. */
  public Color mBaseGColor;

  /** The m base T color. */
  public Color mBaseTColor;

  /** The m base N color. */
  public Color mBaseNColor;

  /**
   * Instantiates a new cytobands.
   */
  private DnaService() {
    updateColors();
  }

  /**
   * Gets the base A color.
   *
   * @return the base A color
   */
  public Color getBaseAColor() {
    return mBaseAColor;
  }

  /**
   * Sets the base A color.
   *
   * @param color
   *          the new base A color
   */
  public void setBaseAColor(Color color) {
    mBaseAColor = color;
    SettingsService.getInstance().update("bioinformatics.dna.bases.a.color", color);
    fireChanged();
  }

  /**
   * Gets the base C color.
   *
   * @return the base C color
   */
  public Color getBaseCColor() {
    return mBaseCColor;
  }

  /**
   * Sets the base C color.
   *
   * @param color
   *          the new base C color
   */
  public void setBaseCColor(Color color) {
    mBaseCColor = color;
    SettingsService.getInstance().update("bioinformatics.dna.bases.c.color", color);
    fireChanged();
  }

  /**
   * Gets the base G color.
   *
   * @return the base G color
   */
  public Color getBaseGColor() {
    return mBaseGColor;
  }

  /**
   * Sets the base G color.
   *
   * @param color
   *          the new base G color
   */
  public void setBaseGColor(Color color) {
    mBaseGColor = color;
    SettingsService.getInstance().update("bioinformatics.dna.bases.g.color", color);
    fireChanged();
  }

  /**
   * Gets the base T color.
   *
   * @return the base T color
   */
  public Color getBaseTColor() {
    return mBaseTColor;
  }

  /**
   * Sets the base T color.
   *
   * @param color
   *          the new base T color
   */
  public void setBaseTColor(Color color) {
    mBaseTColor = color;
    SettingsService.getInstance().update("bioinformatics.dna.bases.t.color", color);

    fireChanged();
  }

  /**
   * Gets the base N color.
   *
   * @return the base N color
   */
  public Color getBaseNColor() {
    return mBaseNColor;
  }

  /**
   * Sets the base N color.
   *
   * @param color
   *          the new base N color
   */
  public void setBaseNColor(Color color) {
    mBaseNColor = color;
    SettingsService.getInstance().update("bioinformatics.dna.bases.n.color", color);
    fireChanged();
  }

  /**
   * Reset the colors back to their defaults.
   */
  public void resetToDefaults() {
    System.err.println("dna reset");

    SettingsService.getInstance().resetToDefault("bioinformatics.dna.bases.a.color");
    SettingsService.getInstance().resetToDefault("bioinformatics.dna.bases.c.color");
    SettingsService.getInstance().resetToDefault("bioinformatics.dna.bases.g.color");
    SettingsService.getInstance().resetToDefault("bioinformatics.dna.bases.t.color");
    SettingsService.getInstance().resetToDefault("bioinformatics.dna.bases.n.color");

    updateColors();
  }

  /**
   * Update colors.
   */
  private void updateColors() {
    mBaseAColor = SettingsService.getInstance().getAsColor("bioinformatics.dna.bases.a.color");

    mBaseCColor = SettingsService.getInstance().getAsColor("bioinformatics.dna.bases.c.color");

    mBaseGColor = SettingsService.getInstance().getAsColor("bioinformatics.dna.bases.g.color");

    mBaseTColor = SettingsService.getInstance().getAsColor("bioinformatics.dna.bases.t.color");

    mBaseNColor = SettingsService.getInstance().getAsColor("bioinformatics.dna.bases.n.color");

    fireChanged();
  }
}