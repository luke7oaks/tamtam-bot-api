/*
 * ------------------------------------------------------------------------
 * TamTam chat Bot API
 * ------------------------------------------------------------------------
 * Copyright (C) 2018 Mail.Ru Group
 * ------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------------------
 */

package chat.tamtam.botapi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * InlineKeyboardAttachmentRequestPayload
 */
public class InlineKeyboardAttachmentRequestPayload implements TamTamSerializable {

    private final List<List<Button>> buttons;

    @JsonCreator
    public InlineKeyboardAttachmentRequestPayload(@JsonProperty("buttons") List<List<Button>> buttons) { 
        this.buttons = buttons;
    }

    /**
    * Two-dimensional array of buttons
    * @return buttons
    **/
    @JsonProperty("buttons")
    public List<List<Button>> getButtons() {
        return buttons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
          return true;
        }
        if (o == null || getClass() != o.getClass()) {
          return false;
        }

        InlineKeyboardAttachmentRequestPayload other = (InlineKeyboardAttachmentRequestPayload) o;
        return Objects.equals(this.buttons, other.buttons);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (buttons != null ? buttons.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "InlineKeyboardAttachmentRequestPayload{"
            + " buttons='" + buttons + '\''
            + '}';
    }
}
