## 组态模块-前端源码

注意：1、主系统和组态对应版本如下：
主 V2.3<=>组 V1.0.0; 主 V2.4<=>组 V2.0.0；主 V2.5.0<=>组 V2.1.0；主 V2.5.1<=>组 V2.2.0；V2.5.2<=>组 V2.3.0；主 V2.6.0<=>组 V2.4.0
<br />
2、组态的图库大部分采集自网络，版权归原作者所有，不属于系统的一部分。图库仅供参考,组态群里获取示例图库。

#### 一、使用方法：

1. 获取源码后，修改文件夹名称为 `scada` ，然后放置于项目的 `wumei-smart/vue/src/views` 文件夹下。

```
完整路径如下：
/wumei-smart/vue/src/views/scada
```

2. 将源码中名称为 `config` 文件夹下的 `scada` 的文件夹剪切到项目`wumei-smart/vue/src/api` 文件夹下

```
完整路径如下：
/wumei-smart/vue/src/api/scada
```

3. 将项目`wumei-smart/vue/src/settings.js`文件里面的`isShowScada`字段值改为**true**

4. 在项目`wumei-smart/vue`文件夹下全局搜索**组态特有**,将注释打开

5. 完成以上步骤后删除此源码中的`config`文件夹

6. 命令窗口执行 `npm install` 安装相关插件

7. 命令窗口执行 `npm run dev` 运行项目即可

### 二、子模块常用命令

```
git clone <repository> --recursive 递归的方式克隆整个项目
git submodule add <repository> <path> 添加子模块
git submodule init 初始化子模块
git submodule update 更新子模块
git submodule foreach git pull 拉取所有子模块
```

### 三、添加子模块

1. 要向主仓库添加子模块，可以使用以下命令：

```
git submodule add <仓库URL> <路径>
```

例如，我们想将一个名为 submodule_repo 的仓库添加到主仓库的 submodule 目录下，可以运行以下命令：

```
git submodule add https://github.com/user/submodule_repo.git submodule
```

这将会在主仓库中添加一个子模块，并将其克隆到 submodule 目录下。

### 四、更新子模块

当子模块的远程仓库发生变化时，我们需要手动更新子模块到最新的版本。可以通过以下两个步骤来完成：

```
git submodule update --remote
```

```
git submodule sync
```

第一步使用了 --remote 参数，它会从远程仓库拉取最新的代码。第二步使用了 sync 命令，它会更新子模块的 URL。

### 五、切换子模块分支

子模块中的分支是独立于主仓库的，因此我们可以在子模块中切换分支，而不会对主仓库产生影响。要在子模块中切换分支，可以使用以下命令：

```
cd submodule
git checkout <分支名>
```

### 六、子模块的提交历史和分支

子模块有自己独立的提交历史和分支，这使得它能够在主仓库的不同版本间保持一致性。我们可以使用 git log 或 git branch 来查看子模块的提交历史和分支情况。
