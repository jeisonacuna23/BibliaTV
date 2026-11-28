using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;

namespace BibliaTV.Windows
{
    public partial class MainWindow : Window
    {
        private List<BibliaLibro> libros = new List<BibliaLibro>();

        public MainWindow()
        {
            InitializeComponent();
            Loaded += MainWindow_Loaded;
        }

        private void MainWindow_Loaded(object sender, RoutedEventArgs e)
        {
            try
            {
                string ruta = Path.Combine(
                    AppDomain.CurrentDomain.BaseDirectory,
                    "Data",
                    "biblia_slim.json"
                );

                if (!File.Exists(ruta))
                    throw new FileNotFoundException(
                        "No se encontró el archivo bíblico.", ruta);

                using (FileStream stream = File.OpenRead(ruta))
                {
                    DataContractJsonSerializer serializer =
                        new DataContractJsonSerializer(typeof(List<BibliaLibro>));

                    libros = (List<BibliaLibro>)serializer.ReadObject(stream);
                }

                BooksList.ItemsSource = libros;
                BooksList.DisplayMemberPath = "Human";

                if (libros.Count > 0)
                    BooksList.SelectedIndex = 0;
            }
            catch (Exception ex)
            {
                MessageBox.Show(
                    "No se pudo cargar la Biblia.\n\n" + ex.Message,
                    "BibliaTV",
                    MessageBoxButton.OK,
                    MessageBoxImage.Error
                );
            }
        }

        private void BooksList_SelectionChanged(
            object sender, SelectionChangedEventArgs e)
        {
            BibliaLibro libro = BooksList.SelectedItem as BibliaLibro;
            if (libro == null || libro.Chapters == null)
                return;

            ChapterSelector.ItemsSource =
                Enumerable.Range(1, libro.Chapters.Count).ToList();

            if (libro.Chapters.Count > 0)
                ChapterSelector.SelectedIndex = 0;
        }

        private void ChapterSelector_SelectionChanged(
            object sender, SelectionChangedEventArgs e)
        {
            RenderChapter();
        }

        private void RenderChapter()
        {
            BibliaLibro libro = BooksList.SelectedItem as BibliaLibro;
            if (libro == null || ChapterSelector.SelectedItem == null)
                return;

            int capitulo = (int)ChapterSelector.SelectedItem;
            if (capitulo < 1 || capitulo > libro.Chapters.Count)
                return;

            CurrentLocation.Text = libro.Human + " " + capitulo;
            ContentPanel.Children.Clear();

            foreach (BibliaItem item in libro.Chapters[capitulo - 1])
            {
                if (item.Type != null && item.Type.StartsWith("heading"))
                {
                    string titulo = UnirLineas(item.Lines);
                    if (!string.IsNullOrWhiteSpace(titulo))
                    {
                        TextBlock heading = new TextBlock
                        {
                            Text = titulo,
                            FontSize = 19,
                            FontWeight = FontWeights.Bold,
                            Foreground = new SolidColorBrush(
                                Color.FromRgb(23, 105, 210)),
                            Margin = new Thickness(0, 12, 0, 14),
                            TextWrapping = TextWrapping.Wrap
                        };
                        ContentPanel.Children.Add(heading);
                    }
                }
                else if (item.Type == "verse")
                {
                    string texto = UnirLineas(item.Lines);
                    string numero = item.VerseNumbers != null &&
                                    item.VerseNumbers.Count > 0
                        ? string.Join(",", item.VerseNumbers)
                        : "";

                    TextBlock verse = new TextBlock
                    {
                        FontSize = 16,
                        Foreground = new SolidColorBrush(
                            Color.FromRgb(23, 43, 77)),
                        TextWrapping = TextWrapping.Wrap,
                        LineHeight = 25,
                        Margin = new Thickness(0, 3, 0, 9)
                    };

                    if (!string.IsNullOrEmpty(numero))
                    {
                        verse.Inlines.Add(new System.Windows.Documents.Run(
                            numero + " ")
                        {
                            FontWeight = FontWeights.Bold,
                            Foreground = new SolidColorBrush(
                                Color.FromRgb(23, 105, 210))
                        });
                    }

                    verse.Inlines.Add(
                        new System.Windows.Documents.Run(texto));

                    ContentPanel.Children.Add(verse);
                }
            }
        }

        private string UnirLineas(List<string> lineas)
        {
            return lineas == null ? "" : string.Join("\n", lineas);
        }

        private void SearchButton_Click(object sender, RoutedEventArgs e)
        {
            SearchBible();
        }

        private void SearchBox_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Enter)
                SearchBible();
        }

        private void SearchBible()
        {
            string consulta = SearchBox.Text.Trim();

            if (consulta.Length < 2)
            {
                MessageBox.Show(
                    "Escribe al menos dos caracteres para buscar.",
                    "Búsqueda",
                    MessageBoxButton.OK,
                    MessageBoxImage.Information
                );
                return;
            }

            ContentPanel.Children.Clear();
            CurrentLocation.Text = "Resultados de búsqueda";

            TextBlock titulo = new TextBlock
            {
                Text = "Buscando: " + consulta,
                FontSize = 20,
                FontWeight = FontWeights.Bold,
                Foreground = new SolidColorBrush(
                    Color.FromRgb(23, 105, 210)),
                Margin = new Thickness(0, 0, 0, 16)
            };
            ContentPanel.Children.Add(titulo);

            int encontrados = 0;
            int maxResultados = 200;

            foreach (BibliaLibro libro in libros)
            {
                for (int c = 0; c < libro.Chapters.Count; c++)
                {
                    foreach (BibliaItem item in libro.Chapters[c])
                    {
                        if (item.Type != "verse")
                            continue;

                        string texto = UnirLineas(item.Lines);

                        if (texto.IndexOf(
                            consulta, StringComparison.OrdinalIgnoreCase) < 0)
                            continue;

                        encontrados++;

                        if (encontrados > maxResultados)
                            continue;

                        int numero = item.VerseNumbers != null &&
                                     item.VerseNumbers.Count > 0
                            ? item.VerseNumbers[0]
                            : 0;

                        Button resultado = new Button
                        {
                            HorizontalContentAlignment =
                                HorizontalAlignment.Left,
                            Background = Brushes.White,
                            BorderBrush = new SolidColorBrush(
                                Color.FromRgb(215, 230, 250)),
                            BorderThickness = new Thickness(1),
                            Padding = new Thickness(12, 9, 12, 9),
                            Margin = new Thickness(0, 0, 0, 7),
                            Cursor = Cursors.Hand,
                            Content = libro.Human + " " + (c + 1) +
                                      ":" + numero + "\n" + texto,
                            Tag = new object[] { libro, c + 1 }
                        };

                        resultado.Click += SearchResult_Click;
                        ContentPanel.Children.Add(resultado);
                    }
                }
            }

            TextBlock total = new TextBlock
            {
                Text = encontrados == 0
                    ? "No se encontraron coincidencias."
                    : "Coincidencias encontradas: " + encontrados +
                      (encontrados > maxResultados
                          ? " (se muestran las primeras " +
                            maxResultados + ")"
                          : ""),
                FontSize = 13,
                Foreground = Brushes.Gray,
                Margin = new Thickness(0, 0, 0, 12)
            };

            ContentPanel.Children.Insert(1, total);
        }

        private void SearchResult_Click(object sender, RoutedEventArgs e)
        {
            Button boton = sender as Button;
            if (boton == null)
                return;

            object[] datos = boton.Tag as object[];
            if (datos == null)
                return;

            BibliaLibro libro = datos[0] as BibliaLibro;
            int capitulo = (int)datos[1];

            BooksList.SelectedItem = libro;
            ChapterSelector.SelectedItem = capitulo;
            RenderChapter();
        }
    }
}
