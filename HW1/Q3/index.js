
   var width = 960 
      height = 500 


var x = d3.scaleBand()
          .range([0, width])
          .padding(0.3);
var y = d3.scaleLinear()
          .range([height, 0]);
          

var svg = d3.select("body").append("svg")
    .attr("width", width  +500)
    .attr("height", height  +900)
    .append("g")
    .attr("transform", "translate(300,0)");

svg.append("text")
       .attr("transform", "translate(100,0)")
       .attr("x", 150)
       .attr("y", 50)
       .attr("fill","black")
       .attr("font-size", "30px")
       .text("Rebrickable Lego Sets by Year")

d3.dsv(",","q3.csv").then(function(dataset){
   dataset.forEach(function(d) {
            d.year = d.year;
            d.running_total = parseInt(d.running_total);
        });
  
  
  x.domain(dataset.map(function(d) { return d.year; }));
  y.domain([0, d3.max(dataset, function(d) { return d.running_total; })]);
  
  svg.selectAll(".bar")
      .data(dataset)
      .enter()
      .append("rect")
      .attr("class", "bar")
      .attr("x", function(d) { return x(d.year); })
      .attr("width", 10)
      .attr("y", function(d) { return y(d.running_total); })
      .attr("height", function(d) { return height - y(d.running_total); });

  
  svg.append("g")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(x).tickValues(x.domain().filter((d, i) => i % 3 === 0 )))
      .append("text")
      .attr("y", 40)
      .attr("x", 450)
      .attr("text-anchor", "middle")
      .attr("fill", "black")
      .attr("font-size",20)
      .text("Year")

  
  svg.append("g")
      .call(d3.axisLeft(y).ticks(30))
      .append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", -50)
      .attr("x",-200)
      .attr("fill", "black")
      .attr("font-size",20)
      .text("Running Total");
;
  svg.append("g")
    .append("text")
    .attr("y", height+100)
    .attr("x", width)
    .attr("text-anchor", "middle")
    .attr("fill", "black")
    .text("mgupta313")
});



