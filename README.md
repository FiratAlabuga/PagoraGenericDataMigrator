# Pagora Generic Data Migrator 🚀

Pagora Migrator, büyük ölçekli verileri (örneğin 250 milyondan fazla satır) farklı veritabanı motorları arasında yüksek hızda, minimum RAM tüketimiyle ve sıfır veri kaybıyla taşımak için tasarlanmış bir **ETL (Extract, Transform, Load)** mikroservisidir.

## 🌟 Temel Özellikler

* **Dinamik Veritabanı Yönetimi:** Kaynak ve hedef veritabanı bağlantılarını uygulamanın içine gömmek yerine, REST API üzerinden çalışma anında (on-the-fly) oluşturur.
* **Büyük Veri (Big Data) Optimizasyonu:** `JdbcCursorItemReader` ve `useCursorFetch=true` konfigürasyonları sayesinde milyonlarca satırlık veriyi RAM'i şişirmeden parça parça okur.
* **Yüksek Hızlı Yazma:** Spring Batch `JdbcBatchItemWriter` ile verileri hedefe 10.000'erli yığınlar (chunk) halinde yazar.
* **Otomatik Şema Çevirisi (Generic Dialect):** Kaynak veritabanındaki (Örn: MySQL) tablo yapılarını, tiplerini ve açıklamalarını evrensel bir tipe (Universal Data Type) çevirir ve hedefte (Örn: PostgreSQL) otomatik olarak yaratır.
* **Asenkron İşlem & Canlı Takip:** API isteği bloklanmaz; süreç arka planda başlar. Anlık olarak kaç satırın taşındığı `/status` API'si üzerinden canlı olarak izlenebilir.
* **Bulut ve Altyapı Entegrasyonları:** İleriye dönük mimaride kullanıcı yönetimi, veritabanı güvenlik kuralları ve dosya depolama işlemleri için **Supabase Auth (RLS)**, **Supabase DB** ve **Supabase Storage** kullanılacak şekilde tasarlanmıştır.

## 🛠️ Teknoloji Yığını

* **Dil:** Java 25+
* **Framework:** Spring Boot 4.x
* **Batch İşleme:** Spring Batch 6.0
* **Veritabanları:** PostgreSQL (Hedef & Metadata), MySQL (Kaynak), SQLite
* **Bağlantı Havuzu:** HikariCP
* **API Dokümantasyonu:** Springdoc OpenAPI (Swagger UI)
* **Konteynerleştirme:** Docker & Docker Compose
* **BaaS (Backend as a Service):** Supabase (Auth, DB, Storage)

## 🚀 Kurulum ve Çalıştırma

### 1. Altyapıyı Ayağa Kaldırma (Docker Compose)
Proje dizininde bulunan `docker-compose.yml` dosyası ile hedef ve kaynak veritabanlarını başlatın:
```bash
docker-compose up -d

Not: Bu komut MySQL'i (source_db) ve PostgreSQL'i (target_db ve migration_batch_db) otomatik olarak kuracaktır.

2. Uygulamayı Başlatma
Gradle kullanarak Spring Boot uygulamasını ayağa kaldırın:

./gradlew bootRun

3. Swagger Arayüzüne Erişim
Uygulama başlatıldıktan sonra API dokümantasyonuna ve test arayüzüne şu adresten ulaşabilirsiniz:
👉 http://localhost:8080/swagger-ui.html

📖 API Kullanımı
1. Migrasyonu Başlatmak
Milyonlarca satırı taşımak için POST /api/v1/migration/start ucuna aşağıdaki JSON formatında istek atın:

{
  "source": {
    "type": "MYSQL",
    "jdbcUrl": "jdbc:mysql://localhost:3306/source_db?useCursorFetch=true",
    "username": "user",
    "password": "password"
  },
  "target": {
    "type": "POSTGRES",
    "jdbcUrl": "jdbc:postgresql://localhost:5432/target_db",
    "username": "postgres",
    "password": "password"
  },
  "settings": {
    "tableName": "users",
    "batchSize": 10000
  }
}


⚠️ Kritik Not: MySQL'den büyük veri çekerken useCursorFetch=true parametresi OutOfMemoryError almamak için zorunludur.

Yanıt (Response): ```text
İşlem başlatıldı! Durumu takip etmek için Job ID: migrationJob_users_5f3b...


### 2. Durum (Status) Takibi
Arka planda çalışan taşıma işleminin durumunu anlık olarak sorgulamak için `GET /api/v1/migration/status/{jobId}` ucunu kullanın:

**Yanıt (Response):**
```json
{
  "jobId": "migrationJob_users_5f3b...",
  "status": "STARTED",
  "readCount": 150000,
  "writeCount": 150000,
  "skipCount": 0,
  "startTime": "2026-03-24T19:15:00.123",
  "endTime": null,
  "exitMessage": ""
}

Gemini şunu dedi:
Markdown
# Pagora Generic Data Migrator 🚀

Pagora Migrator, büyük ölçekli verileri (örneğin 250 milyondan fazla satır) farklı veritabanı motorları arasında yüksek hızda, minimum RAM tüketimiyle ve sıfır veri kaybıyla taşımak için tasarlanmış bir **ETL (Extract, Transform, Load)** mikroservisidir.

## 🌟 Temel Özellikler

* **Dinamik Veritabanı Yönetimi:** Kaynak ve hedef veritabanı bağlantılarını uygulamanın içine gömmek yerine, REST API üzerinden çalışma anında (on-the-fly) oluşturur.
* **Büyük Veri (Big Data) Optimizasyonu:** `JdbcCursorItemReader` ve `useCursorFetch=true` konfigürasyonları sayesinde milyonlarca satırlık veriyi RAM'i şişirmeden parça parça okur.
* **Yüksek Hızlı Yazma:** Spring Batch `JdbcBatchItemWriter` ile verileri hedefe 10.000'erli yığınlar (chunk) halinde yazar.
* **Otomatik Şema Çevirisi (Generic Dialect):** Kaynak veritabanındaki (Örn: MySQL) tablo yapılarını, tiplerini ve açıklamalarını evrensel bir tipe (Universal Data Type) çevirir ve hedefte (Örn: PostgreSQL) otomatik olarak yaratır.
* **Asenkron İşlem & Canlı Takip:** API isteği bloklanmaz; süreç arka planda başlar. Anlık olarak kaç satırın taşındığı `/status` API'si üzerinden canlı olarak izlenebilir.
* **Bulut ve Altyapı Entegrasyonları:** İleriye dönük mimaride kullanıcı yönetimi, veritabanı güvenlik kuralları ve dosya depolama işlemleri için **Supabase Auth (RLS)**, **Supabase DB** ve **Supabase Storage** kullanılacak şekilde tasarlanmıştır.

## 🛠️ Teknoloji Yığını

* **Dil:** Java 17+
* **Framework:** Spring Boot 3.2.x
* **Batch İşleme:** Spring Batch 6.0
* **Veritabanları:** PostgreSQL (Hedef & Metadata), MySQL (Kaynak), SQLite
* **Bağlantı Havuzu:** HikariCP
* **API Dokümantasyonu:** Springdoc OpenAPI (Swagger UI)
* **Konteynerleştirme:** Docker & Docker Compose
* **BaaS (Backend as a Service):** Supabase (Auth, DB, Storage)

## 🚀 Kurulum ve Çalıştırma

### 1. Altyapıyı Ayağa Kaldırma (Docker Compose)
Proje dizininde bulunan `docker-compose.yml` dosyası ile hedef ve kaynak veritabanlarını başlatın:
```bash
docker-compose up -d
Not: Bu komut MySQL'i (source_db) ve PostgreSQL'i (target_db ve migration_batch_db) otomatik olarak kuracaktır.

2. Uygulamayı Başlatma
Gradle kullanarak Spring Boot uygulamasını ayağa kaldırın:

Bash
./gradlew bootRun
3. Swagger Arayüzüne Erişim
Uygulama başlatıldıktan sonra API dokümantasyonuna ve test arayüzüne şu adresten ulaşabilirsiniz:
👉 http://localhost:8080/swagger-ui.html

📖 API Kullanımı
1. Migrasyonu Başlatmak
Milyonlarca satırı taşımak için POST /api/v1/migration/start ucuna aşağıdaki JSON formatında istek atın:

JSON
{
  "source": {
    "type": "MYSQL",
    "jdbcUrl": "jdbc:mysql://localhost:3306/source_db?useCursorFetch=true",
    "username": "user",
    "password": "password"
  },
  "target": {
    "type": "POSTGRES",
    "jdbcUrl": "jdbc:postgresql://localhost:5432/target_db",
    "username": "postgres",
    "password": "password"
  },
  "settings": {
    "tableName": "users",
    "batchSize": 10000
  }
}
⚠️ Kritik Not: MySQL'den büyük veri çekerken useCursorFetch=true parametresi OutOfMemoryError almamak için zorunludur.

Yanıt (Response): ```text
İşlem başlatıldı! Durumu takip etmek için Job ID: migrationJob_users_5f3b...


### 2. Durum (Status) Takibi
Arka planda çalışan taşıma işleminin durumunu anlık olarak sorgulamak için `GET /api/v1/migration/status/{jobId}` ucunu kullanın:

**Yanıt (Response):**
```json
{
  "jobId": "migrationJob_users_5f3b...",
  "status": "STARTED",
  "readCount": 150000,
  "writeCount": 150000,
  "skipCount": 0,
  "startTime": "2026-03-24T19:15:00.123",
  "endTime": null,
  "exitMessage": ""
}
İşlem tamamen bittiğinde status alanı COMPLETED olarak güncellenir.

🧠 Mimari Notlar
JobRepository: Spring Batch 6.0 standartlarına uygun olarak tüm metadata okuma/yazma işlemleri JobExplorer yerine modernize edilmiş JobRepository üzerinden yapılmaktadır.

Universal Type Engine: Sistem veritabanına bağımlı değildir. Yeni bir veritabanı (Örn: Oracle, MSSQL) eklemek için sadece DatabaseDialect arayüzünü implemente etmek yeterlidir.
