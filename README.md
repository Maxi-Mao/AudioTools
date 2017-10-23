# AudioTools
用于音频播放，播放网络或本地音频，一级缓存，存储到sd卡。

博客地址：[http://blog.csdn.net/omrapollo/article/details/78085730](http://blog.csdn.net/omrapollo/article/details/78085730)

# 用法

## 初始化

```
IMAudioManager.instance().init(this);
```

## 使用

### 播放：

```
/* audioUrl音频网络路径 */
IMAudioManager.instance().playSound(audioUrl, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Toast.makeText(MainActivity.this, "播放结束", Toast.LENGTH_SHORT).show();
                    }
                });
```

### 暂停：

```
IMAudioManager.instance().pause();
```

### 继续：

```
IMAudioManager.instance().resume();
```

### 停止：

```
IMAudioManager.instance().release();
```

### 清除缓存：

```
IMAudioManager.instance().delete(new DeleteListener() {
                    @Override
                    public void success() {
                        Toast.makeText(MainActivity.this, "清除成功！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failed(String error) {
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
```

## 说明

```
在调用播放音频的时候，该工具会检测硬盘目录是不是有该存储文件，
如果有则直接播放本地不走流量，如果没有则后台下载，边播边存，以备下次使用。
```

