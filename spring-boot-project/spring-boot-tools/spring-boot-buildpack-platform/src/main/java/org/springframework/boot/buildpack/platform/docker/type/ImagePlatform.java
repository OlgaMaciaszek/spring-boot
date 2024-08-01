/*
 * Copyright 2012-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.buildpack.platform.docker.type;

import java.util.Objects;

import org.springframework.util.Assert;

/**
 * A platform specification for a Docker image.
 *
 * @author Scott Frederick
 * @since 3.4.0
 */
public class ImagePlatform {

	private final String os;

	private final String architecture;

	private final String variant;

	ImagePlatform(String os, String architecture, String variant) {
		Assert.hasText(os, "OS must not be empty");
		this.os = os;
		this.architecture = architecture;
		this.variant = variant;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ImagePlatform that)) {
			return false;
		}
		return Objects.equals(this.os, that.os) && Objects.equals(this.architecture, that.architecture)
				&& Objects.equals(this.variant, that.variant);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.os, this.architecture, this.variant);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.os);
		if (this.architecture != null) {
			builder.append("/").append(this.architecture);
		}
		if (this.variant != null) {
			builder.append("/").append(this.variant);
		}
		return builder.toString();
	}

	/**
	 * Create a new {@link ImagePlatform} from the given value in the form
	 * {@code os[/architecture[/variant]]}.
	 * @param value the value to parse
	 * @return an {@link ImagePlatform} instance
	 */
	public static ImagePlatform of(String value) {
		Assert.hasText(value, "Value must not be empty");
		String[] split = value.split("/+");
		return switch (split.length) {
			case 1 -> new ImagePlatform(split[0], null, null);
			case 2 -> new ImagePlatform(split[0], split[1], null);
			case 3 -> new ImagePlatform(split[0], split[1], split[2]);
			default -> throw new IllegalArgumentException(
					"ImagePlatform value '" + value + "' must be in the form of os[/architecture[/variant]]");
		};
	}

	/**
	 * Create a new {@link ImagePlatform} matching the platform information from the
	 * provided {@link Image}.
	 * @param image the image to get platform information from
	 * @return an {@link ImagePlatform} instance
	 */
	public static ImagePlatform from(Image image) {
		return new ImagePlatform(image.getOs(), image.getArchitecture(), image.getVariant());
	}

}
