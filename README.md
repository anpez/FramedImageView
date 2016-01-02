# FramedImageView
A custom view to show a pannable and scalable image behind a fixed frame.

[ ![Download](https://api.bintray.com/packages/anpez/maven/framedimageview/images/download.svg) ](https://bintray.com/anpez/maven/framedimageview/_latestVersion)

![Snapshot](https://raw.githubusercontent.com/ANPez/FramedImageView/master/snapshot.gif)

## Requirements
Android 4.0, API 14

## Usage
### Gradle dependency

```groovy
dependencies {
  compile 'com.antonionicolaspina.framedimageview:framedimageview:0.1'
}
```

### Sample layout

```xml
<com.antonionicolaspina.framedimageview.FramedImageView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:frame="@drawable/frame"
    app:image="@drawable/sample"/>
```

## License
    Copyright 2015 Antonio Nicolás Pina

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
