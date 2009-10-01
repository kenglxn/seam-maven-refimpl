/* 
 * File        : knowit.Math.js
 * Version     : 1.0
 * Author      : Leif Olsen, leif.olsen@start.no / leif.olsen@knowit.no
 * Copyright   : (c) 2003-2008
 *               Do not use (or abuse) without permission
 * Description : Extensions to standard javascript Math object
 * Notes       : 
 * Dependencies: 
 * Created     : 20031201
 * History     : 20070531 - Modified for community-r
 *             : 20080520 - Modified for knowit
 *             : 20080925 - Moved to separate module: knowit.Math.js
 *               
 */
window.status = 'Loading [knowit.Math.js]';

/**
 * Bernstein's inequality.
 * The inequality is named after Sergei Natanovich Bernstein and finds uses in the field 
 * of approximation theory. Useful for calculating splines.
 * @param {int} t
 * @param {int} n
 * @param {int} i
 * @return {int}
 */
if(!Math.bernstein) {
  Math.bernstein = function(t, n, i) {
    return ( Math.combinations(n,i) * Math.pow(t,i) * Math.pow(1-t,n-i) );
 };
}

/**
 * The number of ways of picking n unordered outcomes from r possibilities
 * @param {int} n
 * @param {int} r
 * @return {int}
 */
if(!Math.combinations) {
  Math.combinations = function(n, r) {
    if(n==0 || r==0) { return 1; }
    return (Math.factorial(n) / (Math.factorial(n-r) * Math.factorial(r)));
 };
}

/**
 * Converts degree value to radiant value.
 * @param {float} d The degree value.
 * @return {float} The rad value
 */
if(!Math.degToRad) {
  Math.degToRad = function(d) {
    return (d*Math.PI) / 180.0;
  };
}

/**
 * The factorial of a natural number n is the product of all positive integers <= n.
 * @param {ints} n The number to foctorize.
 * @return {int} The factorial.
 */
if(!Math.factorial) {
  Math.factorial = function(n) {
    if(n<1) { return 0; }
    var result = 1;
    for(var i=1;i<=n;i++) { result *= i; }
    return result;
  };
}

/**
 * The number of ways of obtaining an ordered subset of k elements from a set of 
 * n elements. The concept of a permutation expresses the idea that distinguishable 
 * objects may be arranged in various different orders 
 * @param {int} n
 * @param {int} k
 * @return {int}
 */
if(!Math.permutations) {
  Math.permutations = function(n, k) {
    if(n==0 || k==0) { return 1; }
    return (Math.factorial(n) / Math.factorial(n-k));
 };
}

/**
 * Converts radiant value to degree value.
 * @param {float} r The rad value.
 * @return {float} The degree value
 */
if(!Math.radToDeg) {
  Math.radToDeg = function(r) {
    return (r*180) / Math.PI; 
  };
}

window.status = '';
