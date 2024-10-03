# pekko-hello-world
Ever since akka became a business license product there was a need for a better open source alternative.  

So Here is the Pekko Framework  

## How does it work

After starting the sample with `sbt run` the following requests can be made:

List all items:

    curl http://localhost:9090/items

Create a item:

    curl -XPOST http://localhost:9090/items -d '{"name": "Bread", "id": 12 }' -H "Content-Type:application/json"

Get the details of one item:

    curl http://localhost:9090/items/Bread

Delete a item:

    curl -XDELETE http://localhost:9090/items/Bread