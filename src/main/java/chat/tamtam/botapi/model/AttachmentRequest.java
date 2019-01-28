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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Objects;


/**
 * Request to attach some data to message
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = PhotoAttachmentRequest.class, name = "image"),
  @JsonSubTypes.Type(value = VideoAttachmentRequest.class, name = "video"),
  @JsonSubTypes.Type(value = AudioAttachmentRequest.class, name = "audio"),
  @JsonSubTypes.Type(value = FileAttachmentRequest.class, name = "file"),
  @JsonSubTypes.Type(value = StickerAttachmentRequest.class, name = "sticker"),
  @JsonSubTypes.Type(value = ContactAttachmentRequest.class, name = "contact"),
  @JsonSubTypes.Type(value = InlineKeyboardAttachmentRequest.class, name = "inline_keyboard"),
})
public class AttachmentRequest implements TamTamSerializable {

    private String type;

    /**
    * @return type
    **/
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
          return true;
        }
        if (o == null || getClass() != o.getClass()) {
          return false;
        }

        AttachmentRequest other = (AttachmentRequest) o;
        return Objects.equals(this.type, other.type);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AttachmentRequest{"
            + " type='" + type + '\''
            + '}';
    }
}

