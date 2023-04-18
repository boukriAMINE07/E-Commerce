<h2>Steps to Start the Project: </h2>
<ol>
  <li>Start ZooKeeper on port 2181:
    <pre><code>start bin\windows\zookeeper-server-start.bat config\zookeeper.properties</code></pre>
  </li>
  <li>Launch Kafka broker on port 9092:
    <pre><code>start bin\windows\kafka-server-start.bat config\server.properties</code></pre>
  </li>
  <li>Start 2 topics, one for category and one for product, in the following order:
    <pre><code>start bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic topic-category</code></pre>
    <pre><code>start bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic topic-product</code></pre>
  </li>
  <li>Uncomment the `@Bean` annotations in the ECommerceApplication.java file to populate the database using the Java Faker library.</li>
  <li>Insert roles into the roles table in the database:
    <pre><code>INSERT INTO roles(name) VALUES('ROLE_USER');</code></pre>
    <pre><code>INSERT INTO roles(name) VALUES('ROLE_ADMIN');</code></pre>
  </li>
  <li>Create a new user by sending a POST request to `/api/auth/signup` with the following request body:
<pre><code>
{
        "username": "xxxx",
        "email": "xxxx@gmail.com",
        "password": "xxxxxx",
        "role": ["admin"]
}
</code></pre>
</li>
  <li>
Authenticate the user by sending a POST request to `/api/auth/signin` with the following request body:
<pre><code>
{
        "username": "xxxx",
        "password": "xxxxxx"
}
</code></pre>
</li>
  <li>Retrieve the token from the response.</li>
</ol>