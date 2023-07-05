WAIBAT
====

平成21年（2009年）の1月から行われた宇宙飛行士の選抜最終試験で受検したWOMBAT-CSを思い出しながら、同年の3月につくったゲームです。

宇宙飛行士の選抜試験ではこんな検査もやっていたんだなぁと、雰囲気を味わっていただけたら幸いです。

（注）コードはすべて独自のもので、WOMBAT-CSを製作しているAERO INNOVATION社とは一切関係がありません。WOMBAT-CSは検査の詳細が公開されていることなどから、WAIBATを公開しても、著作権法、意匠法、不正競争防止法等の法令等には抵触しないものと考えています。AERO INNOVATION社の利益を侵害する意図はなく、同社等から指摘があった場合には公開を取りやめます。また、WAIBATは単なる簡易的なゲームであり、WOMBAT-CSとは、ルール、操作性、点数の計算などさまざまな点が異なります。このゲームに慣れることで適性検査に通りやすくなるといった効果が期待できるものではありません。

## Windows用実行ファイル

Windows用の実行可能ファイルはこちらからダウンロードできます。

[Windows用実行ファイル]()

## 遊び方

### ゲームについて
このゲームは宇宙飛行士の選抜試験で適性検査として使われたWOMBAT-CSを思いだしながらつくりました。

本物のWOMBAT-CSは、
- 空間認識能力
- （その他）認識能力
- ジョイスティックの操作能力
- 並行作業能力
- 緊急時の対応能力

といった航空機のパイロットや宇宙飛行士に求められるさまざまな能力を判断するためのものだそうです（この説明は間違えているかもしれませんので正しくはWOMBAT-CSのサイトをご覧ください）。

楽しんでやるゲームではありませんが、宇宙飛行士の選抜試験ではこういう検査もやっていたんだなぁと、雰囲気を味わっていただけたら幸いです。

### ゲームの目的
このゲームの目的は、さまざまな作業を並行に最大効率でこなすことです。

### キーボード操作
a, s, d, w・・・・・ 左ジョイスティックの代わり  
→, ←, ↑, ↓・・ 右ジョイスティックの代わり  
Space ・・・・・・・ トリガーの代わり  
Enter ・・・・・・・  ボーナスボタンの代わり  
＜＞・・・・・・・・ 図形回転タスクの窓換えボタン（←, →）の代わり  

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/001.png)

### トラッキングタスク
最初の画面はトラッキングタスク（Tracking Task）です。
このタスクの目的は、AUTOTRACKモードにすることです。

まず次の1と2を同時に行います。
 1. 右ジョイスティック（↑↓←→キー）を操作し、黄色の丸を青の丸に入れる
 1. 左ジョイスティック（w, sキー）を操作し、黄色の丸の中に青の縦線を入れる

 ![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/002.png)

1, 2を同時に行っている間にスペースキーを押すと、上部に表示されている「AUTOTRACK」という文字を囲っている白い枠の太さが太くなります。この状態が「AUTOTRACKモード」です。このモードに入ると自動的に1と2の状態が満たされます。つまりジョイスティックを動かさなくても、黄色い丸が青い丸を追跡し、黄色の二本の縦線が青の縦線を追跡します。一定時間経つとこのモードは自動的に解除されてしまいます（後述）。

1と2が同時に満たせていないと、左上のPとWが減っていきます。これは効率が落ちていることを示しており、減点となります。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/003.png)

### ボーナスタスク選択画面
AUTOTRACKモードの状態でリターンキーを押すと、1, 5, 9と番号が書かれている「ボーナス タスク選択画面」に移ります。この画面には次の3種類のボーナス タスク（ボーナス問題）が表示されています。
1. 立体図形回転
<div></div>

5. 数字探し
<div></div>

9. 数字記憶

AUTOTRACKモードの状態で、これら3種の問題を、なるべく均等に、たくさん解答することが重要です。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/004.png)

### 立体図形回転タスク
ボーナス タスクの選択画面で数字の1キーを押すと、立体図形回転（Figure-Rotation） タスクに移動します。ここには2つの立体図形が表示されています。
右ジョイスティック（キーボードの矢印）で図形を回転させることができ、「＜」「＞」キー（Mキーのすぐ右にあります）で、左右どちらの図形を回転させるか選択できます。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/005.png)

このタスクの目的は、左右2つの図形が次のいずれに当てはまるかを判断することです。

- まったく同じ図形
- 鏡面対称
- 異なる図形

2つの図形を回転させて判断し、解答を数字キーで入力しましょう。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/006.png)

問題に正解するとCorrect!と表示されます。ここで数字キーの4を押すと新しい問題が表示されます。正解したら4を押して次々と新しい問題を解答していきましょう。解答数が多いほど得点が上がります。正解する度に右上のPとWが上がり、得点が上がります。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/007.png)

問題に不正解だとIncorrect!と表示されます。不正解の場合、上に表示されている時計がゼロになるまでは次の問題（別のボーナス・タスク）には移れません。なぜ不正解だったかを確認できます。時計がゼロになるまで待ちましょう。時計がゼロに戻ると、自動的にボーナス・タスク選択画面に戻ります。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/008.png)

### AUTOTRACKモードが解除されたら
ボーナス タスクを行っていると、「AUTOTRACK」の背景が赤くなるときがあります。これはAUTOTRACKモードが解除されてしまったことを示します。このような状態になったらすぐにリターンキーを押して、トラッキング タスクに戻ります。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/009.png)

下図のように、AUTOTRACKモードが解除され、黄色い丸が青い丸と線を自動的に追跡しなくなっています。まずスペースキーを押してAUTOTRACKを解除（MANUALモードに戻）し、次にジョイスティックを操作して、また青い丸と線を追跡してください。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/010.png)

Triggerの背景が赤のときは、スペースキーを押してもAUTOTRACKモードにはなりません。 先ほどの1と2を満たした状態にして、赤が消えるまで待ちましょう。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/011.png)

Triggerの赤い背景が消えたら（さきほどの1と2を満たした状態で）スペースキーを押し、AUTOTRACKモードにします。AUTOTRACKモードになったらリターンキーを押して、作業途中のボーナス タスクに戻ります。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/012.png)

### 数字探しタスク

ボーナス タスク選択画面で数字キーの5を押すと、数字探し（Quadrant-Location）タスクの画面に移行します。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/013.png)

数字探しタスクの目的は、1から順に30までの数字を見つけて消すことです。
下の画面のように、1～30までの整数が4つの箱にランダムに入っています。
4つの箱には黄色の背景でそれぞれ8, 9, 5, 6と番号が振ってあります。
まず数字の1を探し、それが入っている箱の番号を押します。下の場合だと1が入っている箱の番号は「9」ですので、数字の9キーを押します。すると1が消えます。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/014.png)

次に数字の2が入っている箱を探すと、番号「5」の箱にありますので、数字キーの5を押します。すると2が消えます。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/015.png)

次に数字の3が入っている箱を探すと、番号「8」の箱にありますので、数字キーの8を押します。すると3が消えます。
こうして次々に1～30までの数字を消していきます。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/016.png)

数字を消すのが早ければ早いほど得点が高くなります。間違えると減点となります。また、ここでもAUTOTRACKモードが解除になる場合があります。AUTOTRACKが解除になったら、リターンキーを押してトラッキングタスク画面に移動し、また頑張ってAUTOTRACKモードにします。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/017.png)

すべての数字を消すことができたら、数字キーの4を押すことで次の問題に進めます。時計がゼロになるまでたくさん問題を解きましょう。
時計がゼロになったら、自動的にボーナスタスク選択画面に戻ります。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/018.png)

### 数字記憶タスク
ボーナス タスク選択画面で9を押すと、数字記憶（Digit-Canceling）タスクの画面に移行します。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/019.png)

数字記憶タスクの目的は、2つ前に表示された数字を入力することです。
最初に数字が表示されます。たとえば、以下の場合「6」です。これを記憶します。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/020.png)

しばらくするとまた数字が表示されます。これも記憶します。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/021.png)

三個めの数字が表示されたら、記憶しておいた『二個前の数字』を入力します。
この例では二個前は「6」でしたので、数字キーの6を押します。同時に、今表示されている数字を記憶します。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/022.png)

次々と数字が表示されるので、そのたびに、記憶しておいた『二個前の数字』を入力していきます。
この例では二個前は「1」でしたので、数字キーの1を押します。
こうして次々に二個前の数字を入力していきます。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/023.png)

このタスクでは、二個前の数字を間違えずに入力することが重要です。数字を忘れてしまったら、適当に数字を入れて続けましょう。
また、AUTOTRACK・モードが解除になる場合もありますので、その際はリターンキーを押して、再度トラッキング タスクで頑張ってAUTOTRACKモードにします。
時計がゼロになると、ボーナス タスク選択画面に戻ります。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/024.png)

### スコア
しばらくするとゲームが終了し、スコアが表示されます。
WOMBAT-CSのスコア計算方法が不明なため、このソフトでは参考程度に表示しています（あてになりません）。また、実際のWOMBAT-CSはプレイ時間がもっと長いです。

![waibat画面説明](https://github.com/yasutakeyohei/waibat/blob/main/readme-imgs/025.png)

## 免責事項
* 本ソフトウェアを使用したことによる一切の損害について開発者は責任を負いません。

## ライセンス
[MIT](/LICENSE)
 
## 作者
[安竹洋平(小平市議)](https://yasutakeyohei.com)
[yasutakeyohei(github)](https://github.com/yasutakeyohei)
[Twitter](https://yasutakeyohei.com)