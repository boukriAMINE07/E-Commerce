<h2>Steps to Start the Project:</h2> 
<ol>
  <li>Start Mysql, ZooKeeper and Kafka broker along with Kafdrop using Docker Compose:
    <pre><code>docker-compose up </code></pre>
  </li>
  <li>Open Kafdrop in your browser at `http://localhost:9000` to monitor Kafka topics.</li>
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
Note: The roles table will be automatically populated with the following values: ROLE_USER and ROLE_ADMIN.
