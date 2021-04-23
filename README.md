# Compass

Compass 是一个简约的指南针应用。 Compass is a simple compass app.

其目标是让用户在手机上也能够使用罗盘。  Its intention is to enable users to use the luopan on their mobile phones.

## 功能和特性 Features

### 自定义皮肤 Customizing Skins

用户可以任意选择手机内的图片作为皮肤，从而自由地更换罗盘类型。 Users can select any picture in their mobile phone as the skin, so that they can freely change the type of the luopan.

我们会[在此](https://github.com/yueyinqiu/Compass-Skins)上传常用的皮肤，如果您有自制的皮肤，欢迎您在 Issues 中进行分享，我们会将其加入（[若因为某些原因无法访问，可点此](#仓库镜像-repository-mirrors)）。 We will upload some frequently-used skins [here](https://github.com/yueyinqiu/Compass-Skins). If you can share your self-made skins in Issues, we will appreciate it and put them into the repository \([click here if cannot access it for some reason](#仓库镜像-repository-mirrors)\).

### 显示传感器精度 Displaying Sensor Accuracy

传感器精度会被显示在标题栏中。其中 `G` 表示加速度传感器的精度，而 `M` 表示磁场传感器的精度。 The accuracies of the sensors are shown in the title bar where `G` represents the accelerometer and `M` represents the magnetic field sensor.

我们建议在使用时保持所有的精度都达到 `4/4` 。 We suggest keeping all the accuracies `4/4` when using it.

### 保持屏幕常亮 Keeping Screen On

当此应用显示在最上层时，屏幕将保持常亮；当应用不在最上层时，将暂停相关传感器的使用以省电。 When this app is on top, the screen will be kept on; If it's not on top, the use of the related sensors will be suspended to save power.

### 锁定指南针 Locking The Compass

通过点击界面以锁定指南针。 Click the interface to lock the compass.

锁定状态下将暂停相关传感器的使用以省电。 If it's locked, the use of the related sensors will be suspended to save power.

## 下载 Downloading

您可以通过以下任意方式下载到安装包： You can download the package in any of the following ways:

- [Github Releases](https://github.com/yueyinqiu/Compass/releases)
- [Gitee 发行版](https://gitee.com/yueyinqiu5990/Compass/releases)

## 使用的库和开源项目 Used Libraries And Open Source Projects

使用的库： Used libraries:
- `com.lqr.picselect:library:1.0.1`

使用的开源代码： Used codes in open source projects:
- [https://github.com/iutinvg/compass](https://github.com/iutinvg/compass/tree/7eb9f12fa96a8e7e7b4d1f133a61c0c0ae5267f4)

## 仓库镜像 Repository Mirrors

由于在部分地区，对于 Github 访问不便，我们也提供 Gitee 的镜像仓库，无特殊情况会保持同步。 Due to the inconvenience of GitHub access in some areas, we also provide mirror repositories on Gitee, which will remain synchronized, if not under some special circumstances.

- Github [Compass](https://github.com/yueyinqiu/Compass.git) -> Gitee [Compass 罗盘](https://gitee.com/yueyinqiu5990/Compass.git)
- Github [Compass-Skins](https://github.com/yueyinqiu/Compass-Skins.git) -> Gitee [Compass-Skins](https://gitee.com/yueyinqiu5990/Compass-Skins.git)