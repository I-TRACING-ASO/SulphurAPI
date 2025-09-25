package v1.sulphurapi.settings;

import burp.api.montoya.ui.settings.SettingsPanelBuilder;
import burp.api.montoya.ui.settings.SettingsPanelPersistence;
import burp.api.montoya.ui.settings.SettingsPanelSetting;
import burp.api.montoya.ui.settings.SettingsPanelWithData;

public class ControlPanel {

     SettingsPanelWithData settingsPanelWithData;

     public ControlPanel() {
          this.settingsPanelWithData = SettingsPanelBuilder.settingsPanel()
                  .withPersistence(SettingsPanelPersistence.PROJECT_SETTINGS)
                  .withTitle("SulphurAPI Settings")
                  .withDescription("Settings for the SulphurAPI extension")
                  .withKeywords("sulphurapi", "API", "api", "auth")
                  .withSetting(
                          SettingsPanelSetting.booleanSetting("Debug Mode", false)
                  )
                  .build();
     }

        public SettingsPanelWithData getSettingsPanel() {
            return this.settingsPanelWithData;
        }

        public Boolean getDebugMode() {
          return (Boolean) this.settingsPanelWithData.getBoolean("Debug Mode");
        }

}
