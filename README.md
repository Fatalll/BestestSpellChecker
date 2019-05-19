# Bestests Spell Check

Простой spell checker принимающий через консольный ввод слово и сообщающий, верно ли оно написано или нет, если нет, предлагающий варианты исправлений.

~~~~
usage: Bestests Spell Check [-c <count>] [-h] [-l <lang>] [-m <metric>]
 -c <count>    suggestions count for word (default 5)
 -h,--help     print this message
 -l <lang>     spell checker language RU or EN (default EN)
 -m <metric>   spell checker metric DL or NW (default DL)
This is simple spell checker for EN and RU languages. You can specify
language from command line. Also you can choose a word score metric from
Damerau-Levenshtein and Needleman-Wunch (based on qwerty keyboard).
~~~~

Пример работы:
~~~~
bigboy@pc~$ bsc -c 3 -l EN -m DL
samebooy ocne tld me

probably mistake in 'samebooy', suggestions:
    somebody
probably mistake in 'ocne', suggestions:
    once
    cone
    one
probably mistake in 'tld', suggestions:
    told
    ltd
correct word 'me'
~~~~

## Использованные технологии

В качестве хранения контейнера для словаря использовался [org.apache.commons.collections4.trie.PatriciaTrie](https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/trie/PatriciaTrie.html). 

В программе используется две метрики, первая - [Расстояние Дамерау — Левенштейна](https://ru.wikipedia.org/wiki/%D0%A0%D0%B0%D1%81%D1%81%D1%82%D0%BE%D1%8F%D0%BD%D0%B8%D0%B5_%D0%94%D0%B0%D0%BC%D0%B5%D1%80%D0%B0%D1%83_%E2%80%94_%D0%9B%D0%B5%D0%B2%D0%B5%D0%BD%D1%88%D1%82%D0%B5%D0%B9%D0%BD%D0%B0),
 вторая - [Алгоритм Нидлмана — Вунша](https://ru.wikipedia.org/wiki/%D0%90%D0%BB%D0%B3%D0%BE%D1%80%D0%B8%D1%82%D0%BC_%D0%9D%D0%B8%D0%B4%D0%BB%D0%BC%D0%B0%D0%BD%D0%B0_%E2%80%94_%D0%92%D1%83%D0%BD%D1%88%D0%B0)
 на основе расстояния между клавишами qwerty клавиатуры. Обе метрики взяты из библиотеки [SimMetrics](https://github.com/Simmetrics/simmetrics).
 
В качестве словарей для английского и русского языка использовался проект [FrequencyWords](https://github.com/hermitdave/FrequencyWords). Были использованы первые 50000 самых частых слов для обоих языков.