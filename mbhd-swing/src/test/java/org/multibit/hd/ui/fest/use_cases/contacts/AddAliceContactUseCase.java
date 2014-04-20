package org.multibit.hd.ui.fest.use_cases.contacts;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.wizards.edit_contact.EditContactState;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "contacts" screen add Alice contact</li>
 * </ul>
 * <p>Requires the "contacts" screen to be showing</p>
 *
 * @since 0.0.1
 *  
 */
public class AddAliceContactUseCase extends AbstractFestUseCase {

  public AddAliceContactUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Get the initial row count
    int rowCount1 = window
      .table(MessageKey.CONTACTS.getKey())
      .contents().length;

    // Click on Add
    window
      .button(MessageKey.ADD.getKey())
      .click();

    // Verify the wizard appears
    window
      .label(MessageKey.NEW_CONTACT_TITLE.getKey());

    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    // Fill in Alice's details
    window
      .textBox(MessageKey.NAME.getKey())
      .setText("Alice");

    window
      .textBox(MessageKey.EMAIL_ADDRESS.getKey())
      .setText("alice@example.org");

    window
      .textBox(MessageKey.BITCOIN_ADDRESS.getKey())
      .setText("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");

    window
      .textBox(MessageKey.EXTENDED_PUBLIC_KEY.getKey())
      .setText("");

    // Ensure Add button is disabled without tag
    addTag("Alice", 0);
    addTag("Bob", 1);
    removeTag(1, 2);

    // Private notes
    window
      .textBox(MessageKey.PRIVATE_NOTES.getKey())
      .setText("Alice's private notes");

    verifyCancel();

    // Click Apply
    window
      .button(MessageKey.APPLY.getKey())
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.ADD.getKey())
      .requireVisible()
      .requireEnabled();

    // Get an updated row count
    int rowCount2 = window
      .table(MessageKey.CONTACTS.getKey())
      .contents().length;

    // Verify a new row has been added
    assertThat(rowCount1 + 1).isEqualTo(rowCount2);

  }

  /**
   * Verifies that clicking cancel with data present gives a Yes/No popover
   */
  private void verifyCancel() {

    // Click Cancel
    window
      .button(MessageKey.CANCEL.getKey())
      .click();

    // Expect Yes/No popup)
    window
      .button(MessageKey.YES.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.CLOSE.getKey())
      .requireVisible()
      .requireEnabled();

    // Click No
    window
      .button(MessageKey.NO.getKey())
      .requireVisible()
      .requireEnabled()
      .click();
  }

  private void addTag(String tag, int startCount) {

    window
      .button(EditContactState.EDIT_CONTACT_ENTER_DETAILS + "." + MessageKey.ADD.getKey())
      .requireVisible()
      .requireDisabled();

    // Add a tag
    window
      .textBox(MessageKey.TAGS.getKey())
      .setText(tag);

    // Count the tags
    final int tagCount1 = window
      .list(MessageKey.TAGS.getKey())
      .contents().length;

    assertThat(tagCount1).isEqualTo(startCount);

    // Click Add tag
    window
      .button(EditContactState.EDIT_CONTACT_ENTER_DETAILS + "." + MessageKey.ADD.getKey())
      .requireVisible()
      .requireEnabled()
      .click();

    // Count the tags
    final int tagCount2 = window
      .list(MessageKey.TAGS.getKey())
      .contents().length;

    assertThat(tagCount2).isEqualTo(tagCount1 + 1);

  }

  private void removeTag(int tagIndex, int startCount) {

    // Count the tags
    final int tagCount1 = window
      .list(MessageKey.TAGS.getKey())
      .contents().length;

    assertThat(tagCount1).isEqualTo(startCount);

    // Click Remove on "tag"
    window
      .list(MessageKey.TAGS.getKey())
      .selectItem(tagIndex);

    // Count the tags
    final int tagCount2 = window
      .list(MessageKey.TAGS.getKey())
      .contents().length;

    assertThat(tagCount2).isEqualTo(startCount- 1);

  }

}