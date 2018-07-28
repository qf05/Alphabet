# Alphabet
## Детская игра "Учимся говорить буквы".

#### Ссылка на Google Play: 

### Описание:

- Эта игра с голосовым управлением поможет вашему ребёнку запомнить алфавит и научит его произносить буквы. 

- Гибкие настройки помогут разнообразить обучающий процесс, и настроить игру индивидуально для вашего ребенка.

- Для распознавания речи используется "Голосовой Ввод Google", по этому перед запуском приложения убедитесь что у вас включена функция "Голосовой Ввод" в настройках вашего Android устройства, а так же подключен интернет или загружен языковой пакет для Русского языка.

- Иногда "Голосовой Ввод Google" не распознаёт отдельные буквы Русского языка, желательно произносить буквы в составе любых слогов.

### Функциональнось:

- Для конвертации голоса в текст, с последующим сравнением: имеется ли в распознаном тексте заданная буква, используется **Google Speech Recognition API**.
- В качестве результатов распознавания используется 3 лучших результата распознования, в которых отфильтрованы слова с количеством уникальных букв превышающих параметр "количество уникальных букв" в настройках приложения.
 Это сделано для того, что бы вы могли общаться с малышом не беспокоясь что ваши слова попадут в список сравнения.
 Например для параметра "количество уникальных букв" = 2:
    - мимимими - это 2 уникальные буквы и будет положительный результат для букв "М" и "И".
    - та на ля - это три слова в каждом по 2 уникальные буквы и будет положительный результат для букв "Т", "А","Н","Л" и "Я".
    - дом - это 3 уникальные буквы, и это слово будет отфильтровано до проверки на правильность результата.
- В качестве поощрения за каждую правильно названную букву стартует мини игра "Лопни шарик". Количество и скорость шариков можно регулировать в настройках приложения.
- Имеется возможность сменить фоновую картинку. Картинка выбирается из галлереии вашего устройства, после чего копируется во внутренюю память телефона. При смене картинки старое изображение удаляется, и на его место копируется новое.
 Если изображение имеет размер более 1080 пикселей в высоту или в ширину, то перед копированием изображение сжимается, с сохранением пропорций, до размеров менее 1080 пикселей в высоту и в ширину. 
- Имеется возможность изменить цвет букв. Для реализации этой функции используется ColorPickerDialog.
- В настройках приложения так же можно отключать и включать вывод текста который используется в качестве проверки на правильность результата. 
 
 
### Скриншоты:

![Alt-текст](https://lh3.googleusercontent.com/jHAu6Dvj-V-JfB8OyxlC8JtEgy-T1R_KM0eKFgoQWS4ZNd-sndI-O3A_8fyjhA-e-W5k=w1366-h658-rw "О")
![Alt-текст](https://lh3.googleusercontent.com/7GOg9D3XvnxriA7kG0ZSDJ2Vwa4PQrJoq5dbvdlPTDtf65waIZ1UHv57s-C0IPX898E=w1366-h658-rw "О")
