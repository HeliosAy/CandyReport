# 🍭 CandyReport
Minecraft sunucunuzda oyuncu raporlama sürecini kolaylaştıran güçlü ve verimli bir Bukkit/Spigot eklentisi.

---
**NOT: Pluginin şuanlık çalışması için MySQL bağlantısını aktif etmeniz gerekmektedir**
## ✨ Özellikler

### 🎯 Temel Özellikler
- Oyuncu raporlama - Basit komutla hızlı raporlama
- Gerçek zamanlı sohbet kaydı - Raporlanan oyuncunun mesajlarını otomatik kayıt
- Yetkililer için GUI - Kullanıcı dostu arayüz ile rapor yönetimi
- BungeeCord desteği - Sunucular arası raporlama sistemi

### ⚡ Performans Optimizasyonları
- Verimli bellek kullanımı - Mesajlar geçici önbelleğe alınır
- MySQL entegrasyonu - Kalıcı veri saklama
- Otomatik temizlik - Düzenli bellek temizliği
- Smart notification - Bungeecord geneli ve tek sunucu için yetkililere bildirim desteği

---

## 📦 Kurulum

1. Plugin dosyasını `plugins/` klasörüne koyun
2. **MySQL veritabanı hazırlayın** (zorunlu!)
3. Sunucuyu başlatın (config dosyaları otomatik oluşturulur)
4. `plugins/CandyReport/config.yml` dosyasında veritabanı bilgilerini girin
5. Sunucuyu yeniden başlatın

---

## ⚙️ Yapılandırma

### Dosya Yapısı
```
plugins/
└── CandyReport/
    ├── config.yml
    └── messages.yml
```

### config.yml Örneği
```yaml
# CandyReport Configuration File
# Author: Berk AYDIN

# BungeeCord network ayarları
bungeecord:
  enabled: false  # true = BungeeCord üzerinden, false = sadece local yetkililere bildirim

# Çoklu sunucu kurulumu için sunucu adı tanımlama
server-name: "Lobi"

# Database Ayarları
# DATABASE OLMADAN PLUGIN CALISMAZ!
database:
  host: "localhost"
  port: 3306
  name: "candyreport"
  username: "root"
  password: ""
  ssl: false

# System Ayarları
report:
  # Oyuncu başına depolanacak maksimum mesaj sayısı
  max-messages-to-store: 4

  # Önceden tanımlanmış rapor nedenleri (oyuncular ayrıca özel nedenler de kullanabilirler)
  reasons:
    - "Küfür Kullanımı"
    - "Spam Yapma"
    - "Toxiclik"
    - "Uygunsuz mesaj"
    - "Reklam"
    - "Hile Kullanımı"
    - "Bug Kullanımı"
    - "Diğer"

    # Bir rapor onaylandığında yürütülecek varsayılan mute komutu
    # Placeholders:
    # {player} = raporlanan oyuncunun adı
  default-mute-command: "mute {player} 1d Sessize Alındınız - Rapor onaylandı"

# Bildirim Ayarları
notification:
  sound:
    enabled: true
    type: "BLOCK_NOTE_BLOCK_PLING"
    volume: 1.0
    pitch: 1.5

# Gui Ayarları
gui:
  title: "&8Aktif Raporlar"
  size: 27  # Sadece (9, 18, 27, 36, 45, 54) kullanılabilir
  # NOT: GUI Size yanlış ayarlanırsa problemlere yol açabilir

# Yetki Ayarları
permissions:
  report: "candyreport.report"      # Rapor oluşturma izni
  view: "candyreport.view"          # Raporları görüntüleme izni
  manage: "candyreport.manage"      # Raporları onaylama/reddetme izni
```

### messages.yml Örneği
```yaml
# CandyReport Mesaj Konfigürasyonu

messages:
  # Genel mesajlar
  no-permission: "&cBunu yapmak için izniniz yok!"
  cannot-report-self: "&cKendinizi raporlayamazsınız!"
  config-reloaded: "&aYapılandırma başarıyla yenilendi!"

  # Rapor mesajları
  report-sent: "&a{player} isimli oyuncuya yaptığınız rapor yetkililere iletildi!"
  report-failed: "&cRapor oluşturulamadı. Lütfen daha sonra tekrar deneyin."
  report-approved: "&a{player} isimli oyuncuya yapılan rapor onaylandı ve işlem yapıldı."
  report-rejected: "&c{player} isimli oyuncuya yapılan rapor reddedildi."

  # Bildirim mesajları
  new-report-notification: |
    &c&l[RAPOR] &f{reported} oyuncusu &f{reporter} tarafından raporlandı
    &cSebep: &f{reason}
    &eRaporları incelemek için &a/rapor view &ekomutunu kullanın!

  # Komut kullanımı
  usage:
    main: |
      &e&lCandyReport Komutları:
      &7/rapor <oyuncu> [sebep] &f- Bir oyuncuyu raporla
      &7/rapor view &f- Bekleyen raporları görüntüle (Yetkili)
      &7/rapor reload &f- Yapılandırmayı yenile (Yönetici)

  # Hata mesajları
  database-error: "&cVeritabanı hatası oluştu. Lütfen bir yönetici ile iletişime geçin."
  unknown-error: "&cBilinmeyen bir hata oluştu. Lütfen tekrar deneyin."

```

---

## 📈 Performans

| Rapor Sayısı | Memory Kullanımı | Veritabanı Boyutu |
|--------------|------------------|-------------------|
| 100 Rapor    | ~Unknow          | ~Unknow            |
| 1000 Rapor   | ~Unknow          | ~Unknow          |
| 10000 Rapor  | ~Unknow         | ~Unknow             |

~~Modern Minecraft sunucuları için bu değerler bence ihmal edilebilir seviyededir.~~

---

## ⚠️ Dikkat Edilmesi Gerekenler

### ✅ Doğru Kullanımlar
- MySQL veritabanı bağlantısını doğru yapılandırın
- BungeeCord kullanıyorsanız tüm sunucularda aynı ayarları yapın
- BungeeCord ayarlarını true yaptıysanız `server-name` ayarlarını her sunucunuz için farklı isim vermelisiniz
  ```yaml
  # Eğer BungeeCord açıksa
  bungeecord:
  enabled: true  # true = BungeeCord üzerinden, false = sadece local yetkililere bildirim

  # Buraya her koyduğunuz sunucu içinn farklı isim vermelisiniz
  server-name: "FarkliIsim"
- Rapor nedenlerini sunucunuza uygun şekilde özelleştirin
- Yetkilere uygun izinleri verin

### ❌ Yanlış Kullanımlar
- MySQL olmadan kullanmaya çalışmak
- Çok fazla mesaj saklama (`max-messages-to-store`)
- Yanlış izin yapılandırması
- BungeeCord ayarlarında tutarsızlık

---

### Komutlar ve Yetkiler

| Komut                      | Açıklama                                       | Yetki                        |
|---------------------------|------------------------------------------------|------------------------------|
| `/rapor <oyuncu> [sebep]` | Oyuncu raporla                                 | config.yml'den ayarlanabilir |
| `/rapor view`             | Raporları görüntüleme GUI'sini aç              |                              |
| `/rapor reload`           | Yapılandırma dosyalarını yeniden yükle         |                              |

---

## 🌐 BungeeCord Entegrasyonu

### Kurulum Adımları
1. `config.yml` içindeki `bungeecord.enabled: true` yapın
2. Tüm Spigot sunucularında CandyReport yükleyin
3. [CandyReportBungee](https://github.com/HeliosAy/CandyReportBungee) pluginini BungeeCord sunucunuza yükleyin
4. Aynı MySQL veritabanını tüm sunucularda kullanın

### Memory Management
- Otomatik mesaj temizliği
- Efficient caching system
- Database connection pooling

---

## 📋 Sistem Gereksinimleri

- **Minecraft**: 1.19+
- **Platform**: Bukkit/Spigot/Paper
- **Veritabanı**: MySQL 5.7+
- **Java**: 17+

---

## 📄 Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

---

🙏 Teşekkürler

---

*Not: Bu plugin benim local test sunucularımda test edilmiştir ve test ortamlarında düzgün çalışmaktadır. Herhangi bir sorunda hata kaydı oluşturabilir veya benimle iletişime geçebilirsiniz...*