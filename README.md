# Circular Progress Image for Jetpack Compose

[![API](https://img.shields.io/badge/API-24%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Version](https://jitpack.io/v/YourUsername/YourRepoName.svg)](https://jitpack.io/#YourUsername/YourRepoName)

A highly customizable circular progress image component for Jetpack Compose. It clips a given image into a sector shape that dynamically changes based on progress, allowing for beautiful and interactive circular progress indicators.

## Preview

**[중요!] 여기에 라이브러리가 동작하는 멋진 GIF나 스크린샷을 꼭 추가하세요!** <br>
*샘플 앱의 예제들을 화면 녹화하여 GIF로 만들면 가장 효과적입니다.*

![Demo GIF](link_to_your_demo.gif)

## Features
- **Custom Shape Clipping:** Clips any `Painter` into a dynamic sector shape.
- **Full Angle Control:** Customize the `startAngle` and `maxSweepAngle` to create full circles, semi-circles, or any arc shape you need.
- **Color Theming:** Easily apply tint colors to both the progress and background images.
- **Stateful Animation:** Comes with a state holder (`rememberCircularProgressState`) for effortless animation control.
- **Stateless Control:** Provides a simple, stateless composable for direct progress manipulation.
- **Robust & Edge-Case Ready:** Smoothly handles all progress values from 0% to 100% without visual glitches.

## Setup

#### 1. Add JitPack repository
Add the JitPack repository to your root `build.gradle.kts` (or `settings.gradle`):
```kotlin
// settings.gradle
dependencyResolutionManagement {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

#### 2. Add the dependency
Add the dependency to your app's `build.gradle.kts`:
```kotlin
dependencies {
    implementation 'com.github.PARAOOO:CircularProgressImage:1.0.0'
}
```

## Usage

Here are some examples demonstrating the capabilities of the library.

### Example 1: Static Progress with a Slider
A simple example showing a static progress value controlled by an external `Slider`.

```kotlin
var progress by remember { mutableFloatStateOf(0.75f) }

CircularProgressImage(
    painter = painterResource(id = R.drawable.your_image),
    progress = progress,
    modifier = Modifier.size(160.dp),
    color = Color.Green,
    backgroundColor = Color.LightGray.copy(alpha = 0.5f)
)

Slider(value = progress, onValueChange = { progress = it })
```

### Example 2: Controlled Animation with Buttons
Use `rememberCircularProgressState` and `AnimatedCircularProgressImage` to control animations programmatically.

```kotlin
val progressState = rememberCircularProgressState(initialProgress = 0f)

AnimatedCircularProgressImage(
    state = progressState,
    painter = painterResource(id = R.drawable.your_image),
    modifier = Modifier.size(160.dp),
    color = Color.Blue,
    backgroundColor = Color(0xFFBBDEFB)
)

Row {
    Button(onClick = { progressState.moveTo(0.25f) }) { Text("25%") }
    Button(onClick = { progressState.moveTo(0.75f) }) { Text("75%") }
    Button(onClick = { progressState.moveTo(1.0f) }) { Text("100%") }
}
```

## API Reference (Parameters for `CircularProgressImage`)

| Parameter           | Type                      | Default Value | Description                                                                               |
| ------------------- | ------------------------- | ------------- | ----------------------------------------------------------------------------------------- |
| `painter`           | `Painter`                 | -             | The main image painter to display and clip.                                               |
| `progress`          | `Float`                   | -             | The current progress, from 0.0f (empty) to 1.0f (full).                                   |
| `modifier`          | `Modifier`                | `Modifier`    | The modifier to be applied to the component.                                              |
| `backgroundPainter` | `Painter?`                | `null`        | An optional painter for the background. If null, `painter` is used for the background.    |
| `startAngle`        | `Float`                   | `0f`          | The starting angle in degrees. 0 degrees is the 12 o'clock position, clockwise.           |
| `maxSweepAngle`     | `Float`                   | `360f`        | The angle in degrees to sweep clockwise from `startAngle` when progress is 1.0f.          |
| `color`             | `Color?`                  | `null`        | An optional tint color to be applied to the progress part of the image.                   |
| `backgroundColor`   | `Color?`                  | `null`        | An optional tint color to be applied to the background part of the image.                 |
| `contentDescription`| `String?`                 | `null`        | Content description for accessibility.                                                    |

## License
```
Copyright 2025 paraooo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUTHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```