package org.multibit.hd.ui.views.wizards.fee_settings;

import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "fee settings" wizard:</p>
 * <ul>
 * <li>Storage of state fee setting panel</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class FeeSettingsPanelModel extends AbstractWizardPanelModel {

  private final Configuration configuration;

  /**
   * @param panelName     The panel name
   * @param configuration The configuration to use
   */
  public FeeSettingsPanelModel(String panelName, Configuration configuration) {
    super(panelName);
    this.configuration = configuration;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

}
