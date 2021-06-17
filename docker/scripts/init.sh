#!/usr/bin/env bash
set -e

mongo <<EOF
  print('Start #################################################################');

  db = db.getSiblingDB("${MONGO_INITDB_DATABASE}");
  db.createUser(
    {
      user: "${DB_USERNAME}",
      pwd: "${DB_PASSWORD}",
      roles: [{ role: 'readWrite', db: "${MONGO_INITDB_DATABASE}" }],
    },
  );
  db.createCollection('category', {
    validator: {
      \$jsonSchema: {
        bsonType: "object",
        required: ["id", "title"],
        properties: {
          id: {
            bsonType: "string",
            description: "must be a string and is required"
          },
          title: {
            bsonType: "string",
            description: "must be a string and is required"
          }
        }
      }
    }
  });

  db.createCollection('video', {
    validator: {
      \$jsonSchema: {
        bsonType: "object",
        required: ["id", "title", "description", "snippet"],
        properties: {
          id: {
            bsonType: "string",
            description: "must be a string and is required"
          },
          title: {
            bsonType: "string",
            description: "must be a string and is required"
          },
          description: {
            bsonType: "string",
            description: "must be a string and is required"
          },
          snippet: {
            bsonType: "object",
            required: ["channelId", "channelTitle", "categoryId", "viewCount", "duration", "thumbnail"],
            properties: {
              channelId: {
                bsonType: "string",
                description: "must be a string and is required"
              },
              channelTitle: {
                bsonType: "string",
                description: "must be a string and is required"
              },
              categoryId: {
                bsonType: "string",
                description: "must be a string and is required"
              },
              viewCount: {
                bsonType: "string",
                description: "must be a string and is required"
              },
              duration: {
                bsonType: "string",
                description: "must be a string and is required"
              },
              thumbnail: {
                bsonType: "object",
                required: ["url", "width", "height"],
                properties: {
                  url: {
                    bsonType: "string",
                    description: "must be a string and is required"
                  },
                  width: {
                    bsonType: "int",
                    description: "must be a integer and is required"
                  },
                  height: {
                    bsonType: "int",
                    description: "must be a integer and is required"
                  }
                }
              }
            }
          }
        }
      }
    }
  });

  db.createCollection('hourlyRank', {
    validator: {
      \$jsonSchema: {
        bsonType: "object",
        required: ["categoryId", "collectedHour", "collectedDate", "videoRanks"],
        properties: {
          categoryId: {
            bsonType: "string",
            description: "must be a integer and is required"
          },
          collectedHour: {
            bsonType: "int",
            minimum: 1,
            maximum: 24,
            description: "must be a integer in [1, 24] and is required"
          },
          collectedDate: {
            bsonType: "string",
            pattern: "^[0-9]{2}-[0-9]{2}-[0-9]{4}$",
            description: "must be a string in dd-MM-yyyy format and is required"
          },
          videoRanks: {
            bsonType: "array",
            description: "must be an array and is required",
            uniqueItems: true,
            additionalProperties: false,
            minItems: 1,
            items: {
              bsonType: "string"
            }
          }
        }
      }
    }
  });

  db.createCollection('dailyRank', {
    validator: {
      \$jsonSchema: {
        bsonType: "object",
        required: ["categoryId", "collectedDate", "videoRanks"],
        properties: {
          categoryId: {
            bsonType: "string",
            description: "must be a integer and is required"
          },
          collectedDate: {
            bsonType: "string",
            pattern: "^[0-9]{2}-[0-9]{2}-[0-9]{4}$",
            description: "must be a string in dd-MM-yyyy format and is required"
          },
          videoRanks: {
            bsonType: "array",
            description: "must be an array and is required",
            uniqueItems: true,
            additionalProperties: false,
            minItems: 1,
            items: {
              bsonType: "string"
            }
          }
        }
      }
    }
  });

  db.category.createIndex( { "id": 1 }, { unique: true });
  db.video.createIndex( { "id": 1 }, { unique: true });
  db.hourlyRank.createIndex( { "categoryId": 1 });
  db.hourlyRank.createIndex( { "categoryId": 1, "collectedDate": 1, "collectedHour": 1 }, { unique: true });
  db.dailyRank.createIndex( { "categoryId": 1 });
  db.dailyRank.createIndex( { "categoryId": 1, "collectedDate": 1 }, { unique: true });

  print('END #################################################################');
EOF