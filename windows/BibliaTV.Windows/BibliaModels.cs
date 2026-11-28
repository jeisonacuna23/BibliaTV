using System.Collections.Generic;
using System.Runtime.Serialization;

namespace BibliaTV.Windows
{
    [DataContract]
    public class BibliaLibro
    {
        [DataMember(Name = "usfm")]
        public string Usfm { get; set; }

        [DataMember(Name = "human")]
        public string Human { get; set; }

        [DataMember(Name = "chapters")]
        public List<List<BibliaItem>> Chapters { get; set; }
    }

    [DataContract]
    public class BibliaItem
    {
        [DataMember(Name = "type")]
        public string Type { get; set; }

        [DataMember(Name = "verse_numbers")]
        public List<int> VerseNumbers { get; set; }

        [DataMember(Name = "lines")]
        public List<string> Lines { get; set; }
    }
}
